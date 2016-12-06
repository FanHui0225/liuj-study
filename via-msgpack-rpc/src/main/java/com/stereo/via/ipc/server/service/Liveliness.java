package com.stereo.via.ipc.server.service;

import com.stereo.via.event.Dispatcher;
import com.stereo.via.event.EventHandler;
import com.stereo.via.ipc.Heartbeat;
import com.stereo.via.ipc.server.event.HeartbeatEvent;
import com.stereo.via.ipc.server.event.enums.HeartbeatEnum;
import com.stereo.via.ipc.util.AbstractLivelinessMonitor;

/**
 * Created by stereo on 16-8-24.
 */
public class Liveliness extends AbstractLivelinessMonitor<Heartbeat> implements EventHandler<HeartbeatEvent>{

    private int expireIntvl;
    private EventHandler dispatcher;

    public Liveliness(int expireIntvl, Dispatcher dispatcher) {
        super("Liveliness");
        this.expireIntvl = expireIntvl;
        this.dispatcher = dispatcher.getEventHandler();
    }

    @Override
    protected void expire(Heartbeat heartbeat) {
    }

    @Override
    protected void serviceInit() throws Exception {
        setExpireInterval(expireIntvl);
        setMonitorInterval(expireIntvl/3);
    }

    @Override
    public void handle(HeartbeatEvent event) {
        HeartbeatEnum type = event.getType();
        switch (type)
        {
            case REGISTER:
                break;
            case UNREGISTER:
                break;
            case PING:
                break;
            case TOPIC_PUSH:
                break;
        }
    }
}
