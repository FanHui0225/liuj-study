package com.stereo.study.ipc.server.skeleton.service;

import com.stereo.study.ipc.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by stereo on 16-8-17.
 */
public class ServiceContext {
    private Packet _request;
    private ChannelHandlerContext _channelHandlerContext;
    private static final ThreadLocal<ServiceContext> _localContext = new ThreadLocal<ServiceContext>();

    protected static void begin(Packet _request, ChannelHandlerContext channelHandlerContext){
        ServiceContext context = (ServiceContext) _localContext.get();
        if (context == null)
        {
            context = new ServiceContext();
            _localContext.set(context);
        }
        context._request = _request;
        context._channelHandlerContext = channelHandlerContext;
    }

    protected static void end() {
        ServiceContext context = (ServiceContext) _localContext.get();
        if (context != null)
        {
            context._request = null;
            context._channelHandlerContext = null;
            _localContext.set(null);
        }
    }

    public static Packet getRequestPacket()
    {
        ServiceContext context = (ServiceContext) _localContext.get();

        if (context != null)
            return context._request;
        else
            return null;
    }

    public static ChannelHandlerContext getChannelHandlerContext()
    {
        ServiceContext context = (ServiceContext) _localContext.get();

        if (context != null)
            return context._channelHandlerContext;
        else
            return null;
    }

    public static Channel getChannel() {
        ServiceContext context = (ServiceContext) _localContext.get();

        if (context != null)
            return context._channelHandlerContext.channel();
        else
            return null;
    }
}
