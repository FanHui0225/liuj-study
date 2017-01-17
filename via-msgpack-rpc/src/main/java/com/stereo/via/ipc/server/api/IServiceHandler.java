package com.stereo.via.ipc.server.api;

import com.stereo.via.event.Event;
import com.stereo.via.event.EventHandler;
import com.stereo.via.ipc.server.event.RequestEvent;
import com.stereo.via.ipc.server.event.ResponseEvent;
import com.stereo.via.ipc.server.event.enums.ServiceEnum;

/**
 * Created by stereo on 16-8-18.
 */
public interface IServiceHandler extends EventHandler<Event<ServiceEnum>> {

    //public void handleHeartbeat(HeartbeatEvent heartbeat) throws Exception;

    public void handleRequest(RequestEvent request) throws Exception;

    public void replyResponse(ResponseEvent response) throws Exception;

    public IServiceInvoker getServiceInvoker();
}
