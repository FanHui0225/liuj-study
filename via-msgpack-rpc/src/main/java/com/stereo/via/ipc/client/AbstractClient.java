package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.Packet;
import com.stereo.via.ipc.codec.MsgPackDecoder;
import com.stereo.via.ipc.codec.MsgPackEncoder;
import com.stereo.via.ipc.exc.ViaRuntimeException;
import com.stereo.via.ipc.remoting.Channel;
import com.stereo.via.ipc.remoting.ChannelHandler;
import com.stereo.via.ipc.remoting.Client;
import com.stereo.via.ipc.remoting.IpcChannel;
import com.stereo.via.service.AbstractService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by stereo on 17-1-19.
 */
public abstract class AbstractClient extends AbstractService implements Client, ChannelHandler {

    private static Logger LOG = LoggerFactory.getLogger(AbstractClient.class);

    private String clientId;
    protected Config config;
    protected Bootstrap bootstrap;
    protected EventLoopGroup group;
    protected volatile boolean closed;
    protected HeartbeatReport heartbeatReport;
    protected volatile io.netty.channel.Channel channel;
    protected final Map<String, Callback> callbackMap = new ConcurrentHashMap<String, Callback>();

    public AbstractClient(String name, Config config) {
        super(name);
        this.config = config;
        this.clientId = UUID.randomUUID().toString();
    }

    @Override
    protected void serviceInit() throws Exception {
       doOpen();
    }

    @Override
    protected void serviceStart() throws Exception {
        doConnect();
        heartbeatReport.start();
    }

    @Override
    protected void serviceStop() throws Exception {
        heartbeatReport.stop();
        doDisConnect();
        doClose();
    }

    protected abstract void doConnect() throws ViaRuntimeException;
    protected abstract void doDisConnect() throws ViaRuntimeException;

    @Override
    public void doOpen() throws ViaRuntimeException {
        //心跳初始化
        heartbeatReport = new HeartbeatReport(this);
        heartbeatReport.init();

        final SslContext sslCtx;
        if (config.isSsl()) {
            try {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                e.printStackTrace();
                throw  new ViaRuntimeException(e);
            }
        } else {
            sslCtx = null;
        }

        Class clazz;
        if(config.isUseEpoll())
        {
            group = new EpollEventLoopGroup(config.getChildNioEventThreads());
            clazz = EpollSocketChannel.class;
        }

        else {
            group = new NioEventLoopGroup(config.getChildNioEventThreads());
            clazz = NioSocketChannel.class;
        }

        bootstrap = new Bootstrap().group(group)
                .channel(clazz)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout())
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
                .option(ChannelOption.SO_LINGER ,config.getSoLinger())
                .option(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, config.getReceiveBufferSize())
                .handler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), config.getHost(), config.getPort()));
                        }
                        p.addLast(
                                new MsgPackEncoder(),
                                new MsgPackDecoder(config.getPayload()),
                                new ClientHandler(AbstractClient.this,config)
                        );
                    }
                });
    }

    @Override
    public void doClose() throws ViaRuntimeException {
        group.shutdownGracefully().syncUninterruptibly();
        releaseCallBack();

    }

    @Override
    public void reconnect() throws ViaRuntimeException {
        doDisConnect();
        doConnect();
    }

    @Override
    public Channel getChannel() {
        io.netty.channel.Channel c = channel;
        if (c == null || ! c.isActive())
            return null;
        return IpcChannel.getOrAddChannel(c, config, this);
    }

    @Override
    public void closeChannel() {
        io.netty.channel.Channel c = channel;
        Channel channel = IpcChannel.getChannel(c);
        channel.closeChannel();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return getChannel().getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return getChannel().isConnected();
    }

    @Override
    public boolean hasAttribute(String key) {
        return getChannel().hasAttribute(key);
    }

    @Override
    public Object getAttribute(String key) {
        return getChannel().getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        getChannel().setAttribute(key,value);
    }

    @Override
    public void removeAttribute(String key) {
        getChannel().removeAttribute(key);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return this;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return getChannel().getLocalAddress();
    }

    @Override
    public void send(Object message) throws ViaRuntimeException {
        getChannel().send(message);
    }

    @Override
    public void send(Object message, boolean sent) throws ViaRuntimeException {
        getChannel().send(message,sent);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public String getClientId() {
        return clientId;
    }

    protected void releaseCallBack(){
        if (getCallbackSize() > 0)
            for (Callback callback : callbackMap.values())
                callback.call(null);
    }

    protected int getCallbackSize() {
        return callbackMap.size();
    }

    protected void setCallback(String messageId, Callback callback) {
        callbackMap.put(messageId, callback);
    }

    protected Callback removeCallBack(String messageId) {
        Callback ret = callbackMap.get(messageId);
        if (ret != null)
            callbackMap.remove(messageId);
        return ret;
    }

//    protected abstract <T extends Packet> AsyncFuture<T>  sendPacket(T packet);
//
//    protected abstract <T extends Packet> AsyncFuture<T> buildFuture(final T packet);

    protected <T extends Packet> AsyncFuture<T>  sendPacket(T packet)
    {
        AsyncFuture<T> future = buildFuture(packet);
        try
        {
            if (!isClosed())
            {
                send(packet,true);
                return future;
            }
            else
                throw new ViaRuntimeException("client sendPacket connect closed");
        }
        catch (Exception ex)
        {
            LOG.error("client >>> send packet error " + "packet : "+ packet , ex);
            removeCallBack(packet.getId());
            throw new ViaRuntimeException("client >>> send packet error " + "packet : "+ packet,ex);
        }
    }

    protected <T extends Packet> AsyncFuture<T> buildFuture(final T packet)
    {
        if (packet !=null && removeCallBack(packet.getId()) == null)
        {
            final AsyncFuture<T> future = new AsyncFuture<T>();
            Callback<T> callback = new Callback<T>() {

                @Override
                public Class<?> getAcceptValueType() {
                    return packet.getClass();
                }

                @Override
                public void call(T value){
                    future.done(value);
                }
            };
            setCallback(packet.getId(), callback);
            return future;
        }else
            throw new ViaRuntimeException("client >>> packet error : " + packet);
    }

    //event
    public void connected(Channel channel) throws ViaRuntimeException
    {
        LOG.info("client channel ["+channel+"] connected");
    }

    /**
     * on channel disconnected.
     *
     * @param channel channel.
     */
    public void disconnected(Channel channel) throws ViaRuntimeException
    {
        LOG.info("client channel ["+channel+"] disconnected");
    }

    /**
     * on message sent.
     *
     * @param channel channel.
     * @param message message.
     */
    public void sent(Channel channel, Object message) throws ViaRuntimeException
    {
        LOG.info("client channel ["+channel+"] sent msg >>> " + message);
    }

    /**
     * on message received.
     *
     * @param channel channel.
     * @param message message.
     */
    public void received(Channel channel, Object message) throws ViaRuntimeException
    {
        if(message instanceof Packet)
        {
            Packet packet = (Packet) message;
            Callback callback = removeCallBack(packet.getId());
            if (callback!=null)
                callback.call(packet);
            else
                LOG.debug("client received packet:" + packet);
            /**
             switch (packet.getType())
             {
             case Constants.TYPE_REQUEST:
             break;
             case Constants.TYPE_RESPONSE:
             notify(packet);
             break;
             case Constants.TYPE_HEARTBEAT:
             break;
             case Constants.TYPE_HEARTBEAT_REQUEST_REGISTER:
             break;
             case Constants.TYPE_HEARTBEAT_REQUEST_UNREGISTER:
             break;
             default:
             LOG.error("ClientHandler.channelRead msg is " + msg);
             }*/
        }
        else
            LOG.warn("client channel received error msg is " + message);
    }

    /**
     * on exception caught.
     *
     * @param channel channel.
     * @param exception exception.
     */
    public void caught(Channel channel, Throwable exception) throws ViaRuntimeException
    {
        LOG.error("caught error msg is " + exception);
        reconnect();
    }
}