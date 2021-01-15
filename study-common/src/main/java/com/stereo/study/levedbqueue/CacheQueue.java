package com.stereo.study.levedbqueue;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by liuj-ai on 2020/3/14.
 */
public interface CacheQueue<E extends CacheRecord> extends AutoCloseable {

    interface CommitCallback {
        void onComplete(boolean committed) throws Exception;
    }

    E getHead();

    E getTail();

    void catchEscape();

    boolean available();

    CacheMeta getCacheMeta();

    E pollHead() throws Exception;

    E takeHead() throws Exception;

    void putTail(E newTail) throws Exception;

    void consumeHead(Consumer<E> consumer) throws Exception;

    void consumeHeads(Consumer<List<E>> consumer) throws Exception;

    void consumeHeads(Consumer<List<E>> consumer, long consumeSize) throws Exception;

    CommitCallback consumeHeadsAndAsyncCommit(Consumer<List<E>> consumer);

    CommitCallback consumeHeadsAndAsyncCommit(Consumer<List<E>> consumer, long consumeSize);
}
