package com.stereo.study.levedbqueue;


import com.stereo.study.util.SerializeHelper;
import com.stereo.study.util.UUID;

import java.io.Serializable;

/**
 * Created by liuj-ai on 2020/3/16.
 */
public final class NodeRecord<T> implements CacheRecord<T>, Serializable {
    private String id;
    private String nextId;
    private byte[] data;
    private Class<?> clazz;

    public static <T> NodeRecord<T> record(T record) {
        return new NodeRecord(record);
    }

    public NodeRecord() {
    }

    public NodeRecord(T record) {
        if (record == null) {
            throw new IllegalArgumentException("data is required");
        }
        fresh();
        this.clazz = record.getClass();
        this.data = SerializeHelper.serialize(record);
    }

    protected NodeRecord fresh() {
        this.id = UUID.randomUUID().toString();
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

    @Override
    public String getNextId() {
        return nextId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public T getRecord() {
        if (this.data != null && this.clazz != null) {
            return SerializeHelper.deserialize(this.data, (Class<T>) this.clazz);
        } else {
            return null;
        }
    }

}
