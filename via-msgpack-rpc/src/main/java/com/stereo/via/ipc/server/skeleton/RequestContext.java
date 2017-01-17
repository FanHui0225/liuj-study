package com.stereo.via.ipc.server.skeleton;

import com.stereo.via.ipc.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by stereo on 16-8-17.
 */
public class RequestContext{
    private Packet _request;
    private ChannelHandlerContext _channelHandlerContext;
    private static final ThreadLocal<RequestContext> _localContext = new ThreadLocal<RequestContext>();

    protected static void begin(Packet _request, ChannelHandlerContext channelHandlerContext){
        RequestContext context = (RequestContext) _localContext.get();
        if (context == null)
        {
            context = new RequestContext();
            _localContext.set(context);
        }
        context._request = _request;
        context._channelHandlerContext = channelHandlerContext;
    }

    protected static void end() {
        RequestContext context = (RequestContext) _localContext.get();
        if (context != null)
        {
            context._request = null;
            context._channelHandlerContext = null;
            _localContext.set(null);
        }
    }

    public static Packet getRequestPacket()
    {
        RequestContext context = (RequestContext) _localContext.get();

        if (context != null)
            return context._request;
        else
            return null;
    }

    public static ChannelHandlerContext getChannelHandlerContext()
    {
        RequestContext context = (RequestContext) _localContext.get();

        if (context != null)
            return context._channelHandlerContext;
        else
            return null;
    }

    public static Channel getChannel() {
        RequestContext context = (RequestContext) _localContext.get();

        if (context != null)
            return context._channelHandlerContext.channel();
        else
            return null;
    }
}
