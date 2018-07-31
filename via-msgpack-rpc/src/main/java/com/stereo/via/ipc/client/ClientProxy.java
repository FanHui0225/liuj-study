package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.exc.ClientConnectException;
import com.stereo.via.ipc.exc.ClientTimeoutException;
import com.stereo.via.ipc.exc.ViaRuntimeException;
import com.stereo.via.ipc.util.NetUtils;
import io.netty.channel.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
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
    protected void doConnect() throws ViaRuntimeException
    {
        try
        {
            ChannelFuture channelFuture = bootstrap.connect(config.getHost(),config.getPort()).syncUninterruptibly();
            boolean ret = channelFuture.awaitUninterruptibly(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
            if (ret && channelFuture.isSuccess())
            {
                channel = channelFuture.channel();
                closed = false;
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
            throw new ViaRuntimeException(ex);
        }
    }

    @Override
    protected void doDisConnect() throws ViaRuntimeException {
        closed = true;
        closeChannel();
        //group.shutdownGracefully().syncUninterruptibly();
    }
}