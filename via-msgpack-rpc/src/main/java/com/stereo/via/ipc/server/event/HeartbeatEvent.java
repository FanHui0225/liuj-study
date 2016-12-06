package com.stereo.via.ipc.server.event;

import com.stereo.via.event.Event;
import com.stereo.via.ipc.Heartbeat;
import com.stereo.via.ipc.server.event.enums.HeartbeatEnum;
import com.stereo.via.ipc.util.Time;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by stereo on 16-8-25.
 */
public class HeartbeatEvent implements Event<HeartbeatEnum> {
    private long timestamp;
    private HeartbeatEnum type;
    private String topic;
    private Object topicData;
    private Heartbeat heartbeat;
    private ChannelHandlerContext channelHandlerContext;

    public HeartbeatEvent(HeartbeatEnum type, String topic, Object topicData)
    {
        this(type,null);
        this.topic = topic;
        this.topicData = topicData;
    }

    public HeartbeatEvent(HeartbeatEnum type, ChannelHandlerContext channelHandlerContext, Heartbeat heartbeat)
    {
        this(type,channelHandlerContext);
        this.heartbeat = heartbeat;
        this.heartbeat.setServer_time(timestamp);
    }

    protected HeartbeatEvent(HeartbeatEnum type, ChannelHandlerContext channelHandlerContext)
    {
        this.type = type;
        this.channelHandlerContext = channelHandlerContext;
        this.timestamp = Time.now();
    }

    @Override
    public HeartbeatEnum getType() {
        return type;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public String getTopic() {
        return topic;
    }

    public Object getTopicData() {
        return topicData;
    }
}
