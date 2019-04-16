package com.stereo.study.ipc.server.event.enums;

/**
 * Created by stereo on 16-8-25.
 */
public enum HeartbeatEnum {
    REGISTER,
    UNREGISTER,
    HEARTBEAT,
    TOPIC_PUSH //server ---> client 推送数据
}
