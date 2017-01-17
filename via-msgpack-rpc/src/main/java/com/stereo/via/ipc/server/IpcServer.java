package com.stereo.via.ipc.server;

import com.stereo.via.ipc.server.api.IServiceContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.codec.MsgPackDecoder;
import com.stereo.via.ipc.codec.MsgPackEncoder;
import com.stereo.via.ipc.server.service.ServiceContext;
import com.stereo.via.service.AbstractService;
import com.stereo.via.service.Service;

/**
 * Created by stereo on 16-8-4.
 */
public class IpcServer extends AbstractService {

    private static Logger log = LoggerFactory.getLogger(IpcServer.class);

    private Config config;
    private Channel channel;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private IServiceContext serviceContext;
    private IpcRegistry registry;

    public IpcServer(){
        this(new Config());
    }

    public IpcServer(Config config) {
        super("IpcServer"+":"+config.getRemoteAddress().toString());
        this.config = config;
    }

    @Override
    protected void serviceInit() throws Exception {
        //业务上下文
        serviceContext = new ServiceContext(config);
        ((Service)serviceContext).init();

        registry = new IpcRegistry(serviceContext);

        final SslContext sslCtx;
        if (config.isSsl()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        Class clazz;
        if(config.isUseEpoll())
        {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(config.getChildNioEventThreads());
            clazz = EpollServerSocketChannel.class;
        }

        else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(config.getChildNioEventThreads());
            clazz = NioServerSocketChannel.class;
        }
        bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(clazz)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                .option(ChannelOption.SO_LINGER ,config.getSoLinger())
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_SNDBUF, config.getSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, config.getReceiveBufferSize())
                .localAddress(config.getRemoteAddress())
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception
                    {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc()));
                        }
                        p.addLast(
                                new MsgPackEncoder(),
                                new MsgPackDecoder(config.getPayload()),
                                new IpcHandler(serviceContext.getDispatcher())
                        );
                    }
                });
    }

    @Override
    protected void serviceStart() throws Exception {
        if (serviceContext!=null)
            ((Service)serviceContext).start();
        if (bootstrap!=null)
        {
            channel = bootstrap.bind(config.getHost(),config.getPort()).sync().channel();
        }
    }

    @Override
    protected void serviceStop() throws Exception {
        if(serviceContext!=null)
            ((Service)serviceContext).stop();
        if(bootstrap!=null && channel!=null && bossGroup!=null && workerGroup!=null)
        {
            channel.close().sync();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            bootstrap = null;
            channel = null;
            bossGroup = null;
            workerGroup = null;

        }
    }

    public IpcRegistry getIpcRegistry(){
        return registry;
    }

    public Config getConfig() {
        return config;
    }

    public IServiceContext getServiceContext() {
        return serviceContext;
    }
}
