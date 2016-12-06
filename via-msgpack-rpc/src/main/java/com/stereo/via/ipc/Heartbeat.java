package com.stereo.via.ipc;

import org.msgpack.BeanMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by stereo on 16-8-15.
 */
public class Heartbeat implements BeanMessage {
    private String client_id;
    private byte type;
    private long client_time;
    private long server_time;
    private List<String> topics;
    private Map<String,List<Object>> data;

    public String getClient_id() {
        return client_id;
    }

    public byte getType() {
        return type;
    }

    public long getClient_time() {
        return client_time;
    }

    public long getServer_time() {
        return server_time;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setClient_time(long client_time) {
        this.client_time = client_time;
    }

    public void setServer_time(long server_time) {
        this.server_time = server_time;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Map<String, List<Object>> getData() {
        return data;
    }

    public void setData(Map<String, List<Object>> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heartbeat heartbeat = (Heartbeat) o;
        return client_id.equals(heartbeat.client_id);
    }

    @Override
    public int hashCode() {
        return client_id.hashCode();
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "client_id='" + client_id + '\'' +
                ", type=" + type +
                ", client_time=" + client_time +
                ", server_time=" + server_time +
                ", topics=" + topics +
                ", data=" + data +
                '}';
    }
}
