package com.stereo.via.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 参考hbase RpcServer，编写了一个简洁版多Selector server，
 * 对nio怎么用，Selector如何选择事件会有更深入的认识。
 * <p>
 * Created by liuj-ai on 2018/2/2.
 */
public class Client {

    public static final Log LOG = LogFactory.getLog(Client.class);

    Socket socket;
    SocketChannel socketChannel;
    Selector selector;

    public Client() throws IOException {
        InetSocketAddress server = new InetSocketAddress("localhost", 10000);
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(server);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT );

        out:
        while (true) {
            int ret = selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            if (ret > 0) {
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isValid()) {
                        if (key.isConnectable()) {
                            if (socketChannel.finishConnect()) {
                                break out;
                            }
                        }
                    }
                }
            }
        }
        socket = socketChannel.socket();
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
    }

    public void send(String message) throws IOException {
        byte[] data = message.getBytes();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE).attach(byteBuffer);
    }

    private static int NIO_BUFFER_LIMIT = 8 * 1024; //限制64KB

    public static void main(String[] args) throws IOException {
        final int n = 1;
        for (int i = 0; i < n; i++) {
            new Thread() {
                Client client = new Client();
                public void run() {
                    Selector selector = client.selector;
                    try {
                        for (int j = 0; j < 3; j++)
                        {
                            //写
                            client.send(getName() + "_liujing");
                            //读
                            ByteBuffer lenBuf = ByteBuffer.allocate(4);
                            ByteBuffer readBuf = null;
                            loop:
                            while (true) {
                                int ret = client.selector.select();
                                if (ret > 0) {
                                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                                    loop_selectionkey:
                                    while (it.hasNext()) {
                                        SelectionKey key = it.next();
                                        it.remove();
                                        if (key.isValid()) {
                                            SocketChannel socketChannel = (SocketChannel)key.channel();
                                            if (key.isReadable()) {
                                                //读取dataLen
                                                if (lenBuf.remaining() > 0) {
                                                    int count = socketChannel.read(lenBuf);
                                                    if (count < 0 || lenBuf.remaining() > 0)
                                                        break loop_selectionkey;
                                                }

                                                if (readBuf == null) {
                                                    lenBuf.flip();
                                                    //获取数据长度
                                                    int dataLength = lenBuf.getInt();
                                                    //创建数据缓存
                                                    readBuf = ByteBuffer.allocate(dataLength);
                                                    LOG.info("receive from server: dataLength=" + dataLength);
                                                }

                                                //读取数据限制64KB
                                                int limit = readBuf.limit();
                                                limit64KB_break:
                                                while (readBuf.hasRemaining()) {
                                                    try {
                                                        int ioSize = Math.min(readBuf.remaining(), NIO_BUFFER_LIMIT);
                                                        readBuf.limit(readBuf.position() + ioSize);
                                                        ret = socketChannel.read(readBuf);
                                                        if (ret < ioSize) {
                                                            break limit64KB_break;
                                                        }
                                                    } finally {
                                                        readBuf.limit(limit);
                                                    }
                                                }
                                                if (readBuf.remaining() == 0) {
                                                    lenBuf.clear();
                                                    readBuf.flip();

                                                    //处理dataBuf
                                                    LOG.info("receive from server: data=" + readBuf.array());
                                                    readBuf = null;
                                                    break loop;
                                                }
                                            }else if (key.isWritable())
                                            {
                                                ByteBuffer writeBuf = (ByteBuffer)key.attachment();
                                                int limit = writeBuf.limit();
                                                limit64KB_break:
                                                while (writeBuf.hasRemaining()) {
                                                    try {
                                                        int ioSize = Math.min(writeBuf.remaining(), NIO_BUFFER_LIMIT);
                                                        writeBuf.limit(writeBuf.position() + ioSize);
                                                        ret = socketChannel.write(writeBuf);
                                                        if (ret < ioSize) {
                                                            break limit64KB_break;
                                                        }
                                                    } finally {
                                                        writeBuf.limit(limit);
                                                    }
                                                }
                                                if (writeBuf.remaining() == 0) {
                                                    LOG.info("send to server: dataLen=" + limit);
                                                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        client.socketChannel.close();
                    } catch (IOException e) {
                        LOG.error("", e);
                    } catch (Exception e) {
                        LOG.error("", e);
                    }
                }
            }.start();
        }
    }

}
