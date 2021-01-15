package com.stereo.study.levedbqueue;

/**
 * Created by liuj-ai on 2020/3/31.
 */
public interface MutlCacheQueue<E extends CacheRecord> extends AutoCloseable {

    CacheQueue<E> getQueue(String cacheQueueId);
}
