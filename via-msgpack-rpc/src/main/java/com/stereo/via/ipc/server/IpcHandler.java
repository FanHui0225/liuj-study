package com.stereo.via.ipc.server;

import com.stereo.via.event.Dispatcher;
import com.stereo.via.ipc.Constants;
import com.stereo.via.ipc.Heartbeat;
import com.stereo.via.ipc.Packet;
import com.stereo.via.ipc.server.api.IpcEngine;
import com.stereo.via.ipc.server.event.RequestEvent;
import com.stereo.via.ipc.server.event.ResponseEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stereo on 16-8-9.
 */
public class IpcHandler extends ChannelInboundHandlerAdapter implements IpcEngine {

    private static Logger LOG = LoggerFactory.getLogger(IpcHandler.class);

    private Dispatcher dispatcher;
    public IpcHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try
        {
            if(msg != null  && msg instanceof Packet)
            {
                final Packet packet = (Packet) msg;
                byte type = packet.getType();
                switch (type)
                {
                    case Constants.TYPE_REQUEST:
                        dispatcher.getEventHandler().handle(new RequestEvent(packet,ctx));
                        break;
                    case Constants.TYPE_RESPONSE:
                        dispatcher.getEventHandler().handle(new ResponseEvent(packet,ctx));
                        break;
                    case Constants.TYPE_HEARTBEAT_REQUEST:
                        break;
                    case Constants.TYPE_HEARTBEAT_RESPONSE:
                        break;
                    case Constants.TYPE_HEARTBEAT_REQUEST_UNREGISTER:
                        break;
                    default:
                        LOG.error("IpcHandler.channelRead error msg is " + msg);
                }
            } else
                LOG.error("IpcHandler.channelRead error msg is " + msg);
        } catch (Exception e)
        {
            LOG.error("IpcHandler.handle packet is " + msg + " error",e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("IpcHandler.exceptionCaught",cause);
        ctx.close();
    }
}
