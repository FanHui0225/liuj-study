package com.stereo.study.levedbqueue;

import java.io.Serializable;

/**
 * Created by liuj-ai on 2020/3/14.
 */
public interface CacheRecord<T> extends Serializable {
    String getId();

    void setId(String id);

    String getNextId();

    void setNextId(String nextId);

    T getRecord();
}
