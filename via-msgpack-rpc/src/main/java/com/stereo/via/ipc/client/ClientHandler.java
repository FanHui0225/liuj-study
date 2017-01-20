package com.stereo.via.ipc.client;

import com.stereo.via.ipc.remoting.ChannelHandler;
import com.stereo.via.ipc.remoting.IpcChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stereo on 16-8-4.
 */

public class ClientHandler extends ChannelInboundHandlerAdapter{

    private static Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        IpcChannel channel = IpcChannel.getChannel(ctx.channel());
        try {
            if (channel != null)
            {
                channel.disconnected(channel);
            }
        } finally {
            IpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        IpcChannel channel = IpcChannel.getChannel(ctx.channel());
        try {
            if (channel != null)
            {
                channel.connected(channel);
            }
        } finally {
            IpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        //LOG.debug("ClientHandler.channelRead msg is " + msg);
        IpcChannel channel = IpcChannel.getChannel(ctx.channel());
        try
        {
            if (channel != null)
            {
                channel.received(channel,msg);
            }
        } finally {
            IpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        LOG.error("ClientHandler.exceptionCaught",cause);
        IpcChannel channel = IpcChannel.getChannel(ctx.channel());
        try
        {
            if (channel != null)
            {
                channel.caught(channel,cause);
            }
        } finally {
            IpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}