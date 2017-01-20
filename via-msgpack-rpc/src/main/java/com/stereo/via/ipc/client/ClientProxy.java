package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.Packet;
import com.stereo.via.ipc.codec.MsgPackDecoder;
import com.stereo.via.ipc.codec.MsgPackEncoder;
import com.stereo.via.ipc.exc.ClientConnectException;
import com.stereo.via.ipc.exc.ClientTimeoutException;
import com.stereo.via.ipc.exc.IpcRuntimeException;
import com.stereo.via.ipc.util.NetUtils;
import com.stereo.via.service.AbstractService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * IPC客户端代理
 * Created by stereo on 16-8-4.
 */
public class ClientProxy extends AbstractClient {

    private final ClassLoader loader;

    public ClientProxy(Config config){
        this(config, Thread.currentThread().getContextClassLoader());
    }

    public ClientProxy(Config config , ClassLoader  loader)
    {
        super("ClientProxy"+":"+config.getRemoteAddress().toString(),config);
        this.loader = loader;
    }

    public <T> T create(final Class<T> api)  throws Exception {
        return create(api,loader);
    }

    public <T> T create(Class<?> api ,ClassLoader classLoader) throws Exception {
        InvocationHandler invocationHandler = new RemoteProxy(this, api);
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { api }, invocationHandler);
    }

    @Override
    public void doConnect() throws IpcRuntimeException
    {
        try
        {
            ChannelFuture channelFuture = bootstrap.connect(config.getHost(),config.getPort()).sync();
            channelFuture.awaitUninterruptibly(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess())
            {
                channel = channelFuture.channel();
                if (NetUtils.toAddressString((InetSocketAddress) channel.remoteAddress())
                        .equals(NetUtils.toAddressString((InetSocketAddress) channel.localAddress()))) {
                    closeChannel();
                    throw new ClientConnectException("Failed to connect " + config.getHost() + ":" + config.getPort()
                            + ". Cause by: Remote and local address are the same");
                }
            }else {
                throw new ClientTimeoutException(channelFuture.cause());
            }
        }catch (Exception ex)
        {
            throw new IpcRuntimeException(ex);
        }
    }

    @Override
    public void doDisConnect() throws IpcRuntimeException {
        closeChannel();
        //group.shutdownGracefully().syncUninterruptibly();
    }
}