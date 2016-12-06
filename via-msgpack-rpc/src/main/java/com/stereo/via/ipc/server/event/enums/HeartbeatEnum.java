package com.stereo.via.ipc.server.event.enums;

/**
 * Created by stereo on 16-8-25.
 */
public enum HeartbeatEnum {
    REGISTER,
    UNREGISTER,
    PING,
    TOPIC_PUSH //server ---> client 推送数据
}
