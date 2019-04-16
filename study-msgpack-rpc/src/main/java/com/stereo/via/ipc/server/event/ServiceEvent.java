package com.stereo.via.ipc.server.event;

import io.netty.channel.ChannelHandlerContext;
import com.stereo.via.event.Event;
import com.stereo.via.ipc.server.event.enums.ServiceEnum;
import com.stereo.via.ipc.util.Time;

/**
 * Created by stereo on 16-8-18.
 */
public class ServiceEvent<T> implements Event<ServiceEnum> {
    private T target;
    private long timestamp;
    private ServiceEnum type;
    private ChannelHandlerContext channelHandlerContext;

    public ServiceEvent(T target, ServiceEnum type, ChannelHandlerContext channelHandlerContext){
        this.timestamp = Time.now();
        this.target = target;
        this.type = type;
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public ServiceEnum getType() {
        return type;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public T getTarget() {
        return target;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
