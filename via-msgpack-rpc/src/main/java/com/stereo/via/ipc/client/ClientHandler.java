package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Config;
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

    Config config;
    AbstractClient client;
    protected ClientHandler(AbstractClient client , Config config)
    {
        this.client = client;
        this.config = config;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        IpcChannel channel = IpcChannel.getOrAddChannel(ctx.channel(), config, client);
        if (channel != null)
        {
            channel.disconnected(channel);
        }else
            LOG.warn("ClientHandler channelInactive channel is null");
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        IpcChannel channel = IpcChannel.getOrAddChannel(ctx.channel(), config, client);
        if (channel != null)
        {
            channel.connected(channel);
        }
        else
            LOG.warn("ClientHandler channelActive channel is null");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        //LOG.debug("ClientHandler.channelRead msg is " + msg);
        IpcChannel channel = IpcChannel.getOrAddChannel(ctx.channel(), config, client);
        if (channel != null)
        {
            channel.received(channel,msg);
        }
        else
            LOG.warn("ClientHandler channelRead channel is null");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        LOG.error("ClientHandler.exceptionCaught",cause);
        IpcChannel channel = IpcChannel.getOrAddChannel(ctx.channel(), config, client);
        if (channel != null)
        {
            channel.caught(channel,cause);
        }else
            LOG.warn("ClientHandler exceptionCaught channel is null");
    }
}