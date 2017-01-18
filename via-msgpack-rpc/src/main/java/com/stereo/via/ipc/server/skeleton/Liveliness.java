package com.stereo.via.ipc.server.skeleton;

import com.stereo.via.event.EventHandler;
import com.stereo.via.ipc.Heartbeat;
import com.stereo.via.ipc.server.event.HeartbeatEvent;
import com.stereo.via.ipc.server.event.enums.HeartbeatEnum;
import com.stereo.via.ipc.util.AbstractLivelinessMonitor;
import com.stereo.via.ipc.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by stereo on 16-8-24.
 */
public class Liveliness extends AbstractLivelinessMonitor<Heartbeat> implements EventHandler<HeartbeatEvent>{

    private static Logger LOG = LoggerFactory.getLogger(Liveliness.class);

    private int expireIntvl;
    private LiveExpired liveExpired;

    public Liveliness(int expireIntvl,LiveExpired liveExpired) {
        super("Liveliness");
        this.expireIntvl = expireIntvl;
        this.liveExpired = liveExpired;
    }

    public Set<Heartbeat> living()
    {
        return Collections.unmodifiableSet(running.keySet());
    }

    @Override
    protected void expire(Heartbeat heartbeat) {
        if (liveExpired == null)
            LOG.info("client " + heartbeat.getClient_id() + " expired");
        else
            liveExpired.expire(heartbeat);
    }

    @Override
    protected void serviceInit() throws Exception {
        setExpireInterval(expireIntvl);
        setMonitorInterval(expireIntvl/3);
    }

    @Override
    public void handle(HeartbeatEvent event)
    {
        HeartbeatEnum type = event.getType();
        Heartbeat heartbeat = event.getHeartbeat();
        switch (type)
        {
            case REGISTER:
                register(heartbeat);
                break;
            case UNREGISTER:
                unregister(heartbeat);
                break;
            case HEARTBEAT:
                if (running.containsKey(heartbeat))
                    receivedPing(heartbeat);
                else
                    register(heartbeat);
                break;
            case TOPIC_PUSH:
                break;
        }
        heartbeat.setServer_time(Time.now());
        try {
            event.getChannelHandlerContext().channel().writeAndFlush(event.getPacket()).sync();
        } catch (InterruptedException e) {
            LOG.error(getName() + " reply heartbeat faile");
        }
    }
}
