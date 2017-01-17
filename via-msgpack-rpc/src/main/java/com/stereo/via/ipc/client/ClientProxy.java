package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.codec.MsgPackDecoder;
import com.stereo.via.ipc.codec.MsgPackEncoder;
import com.stereo.via.ipc.exc.ClientConnectException;
import com.stereo.via.ipc.exc.ClientTimeoutException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by stereo on 16-8-4.
 */
public class ClientProxy extends AbstractService {

    private static Logger LOG = LoggerFactory.getLogger(ClientProxy.class);

    private Config config;

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    private Channel channel;

    private final ClassLoader loader;

    public ClientProxy(Config config){
        this(config, Thread.currentThread().getContextClassLoader());
    }

    private final Map<String, Callback> callbackMap = new ConcurrentHashMap<String, Callback>();

    public ClientProxy(Config config , ClassLoader  loader)
    {
        super("ClientProxy"+":"+config.getRemoteAddress().toString());
        this.config = config;
        this.loader = loader;
    }

    @Override
    protected void serviceInit() throws Exception
    {
        final SslContext sslCtx;
        if (config.isSsl()) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
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
                                new ClientHandler(ClientProxy.this)
                        );
                    }
                });
    }

    @Override
    protected void serviceStart() throws Exception {
        if(bootstrap!=null)
        {
            ChannelFuture channelFuture = bootstrap.connect(config.getHost(),config.getPort()).sync();
            channelFuture.awaitUninterruptibly(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess())
            {
                channel = channelFuture.channel();
                if (NetUtils.toAddressString((InetSocketAddress) channel.remoteAddress())
                        .equals(NetUtils.toAddressString((InetSocketAddress) channel.localAddress()))) {
                    channel.close();
                    throw new ClientConnectException("Failed to connect " + config.getHost() + ":" + config.getPort()
                            + ". Cause by: Remote and local address are the same");
                }
            }else {
                throw new ClientTimeoutException(channelFuture.cause());
            }
        }
    }

    @Override
    protected void serviceStop() throws Exception {
        if(channel!=null && group != null)
        {
            channel.close().sync();
            group.shutdownGracefully();
            bootstrap = null;
            group = null;
            channel = null;
        }
    }

    public <T> T create(final Class<T> api)  throws Exception {
        return create(api,loader);
    }

    public <T> T create(Class<?> api ,ClassLoader classLoader) throws Exception {
        InvocationHandler invocationHandler = new RemoteProxy(this, api);
        return (T) Proxy.newProxyInstance(classLoader, new Class[] { api }, invocationHandler);
    }

    protected void releaseCallBack() throws Exception {
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

    public Channel getChannel() {
        return channel;
    }

    public Config getConfig() {
        return config;
    }
}
