package com.stereo.study.levedbqueue;

import com.stereo.study.util.KV;
import com.stereo.study.util.MixAll;
import com.stereo.study.util.SerializeHelper;
import org.iq80.leveldb.WriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 单链表阻塞队列
 * 基于文件存储,实现阻塞式链表队列.
 * 需要改进(插入吞吐需要CAS+伪共享改进性能)
 * <p>
 * Created by liuj-ai on 2020/3/16.
 */
public class LinkedCacheQueue<T> implements CacheQueue<NodeRecord<T>> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LinkedCacheQueue.class);

    /* 无可消费回调 */
    private static final NoConsumeCommitCallback NO_CONSUME_COMMIT_CALLBACK = new NoConsumeCommitCallback();

    public static class NoConsumeCommitCallback implements CommitCallback {
        @Override
        public void onComplete(boolean committed) throws Exception {
            // No Consume
        }
    }

    final String cacheQueueId;
    final CacheMeta cacheMeta;
    final CacheStore cacheStore;
    final ReentrantLock lock;
    private final Condition notEmpty;

    public LinkedCacheQueue(String cacheQueueId) {
        this(cacheQueueId, new CacheStore(), false);
    }

    public LinkedCacheQueue(String cacheQueueId, boolean fair) {
        this(cacheQueueId, new CacheStore(), fair);
    }

    public LinkedCacheQueue(String cacheQueueId, String cachePath, boolean fair) {
        this(cacheQueueId, new CacheStore(cachePath, true), fair);
    }

    public LinkedCacheQueue(String cacheQueueId, String cachePath, boolean cachePathPreClean, boolean fair) {
        this(cacheQueueId, new CacheStore(cachePath, cachePathPreClean), fair);
    }

    public LinkedCacheQueue(String cacheQueueId, CacheStore cacheStore, boolean fair) {
        if (cacheQueueId == null) {
            throw new IllegalArgumentException("cacheQueueId required");
        }
        if (cacheStore == null) {
            throw new IllegalArgumentException("cacheStore required");
        }
        this.cacheQueueId = cacheQueueId;
        this.lock = new ReentrantLock(fair);
        this.notEmpty = lock.newCondition();
        this.cacheStore = cacheStore;
        this.cacheMeta = new CacheMeta(cacheQueueId);
        this.init();
    }

    /**
     * 初始化缓存队列
     */
    protected void init() {
        //初始化缓存
        this.cacheStore.init();
        //加载meta信息
        this.cacheMeta.load(cacheStore);
        //试图捕获逃逸数据
        this.catchEscape();
    }

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.lock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * 数据入队
     *
     * @param newTail
     * @throws InterruptedException
     */
    public void putTail(NodeRecord<T> newTail) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            String recordId = newTail.getId();
            if (cacheMeta.tailRecordId == null) {
                cacheMeta.headRecordId = cacheMeta.tailRecordId = recordId;
                enqueue(null, newTail);
            } else {
                NodeRecord<T> currentTail = getTail();
                currentTail.setNextId(recordId);
                cacheMeta.tailRecordId = recordId;
                enqueue(currentTail, newTail);
            }
            signalNotEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 进队
     *
     * @param currentTail
     * @param newTail
     */
    private void enqueue(NodeRecord<T> currentTail, NodeRecord<T> newTail) {
        KV<byte[], byte[]> metaKV = cacheMeta.toBytes();
        WriteBatch writeBatch = cacheStore.createWriteBatch();
        writeBatch.put(metaKV.key(), metaKV.value());
        if (currentTail != null) {
            writeBatch.put(SerializeHelper.serialize(currentTail.getId()), (SerializeHelper.serialize(currentTail)));
        }
        writeBatch.put(SerializeHelper.serialize(newTail.getId()), (SerializeHelper.serialize(newTail)));
        cacheStore.writeSync(writeBatch);
    }

    /**
     * 数据出队
     *
     * @return
     * @throws InterruptedException
     */
    public NodeRecord<T> pollHead() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (cacheMeta.headRecordId == null) {
                return null;
            } else {
                NodeRecord<T> head = getHead();
                dequeue(head);
                return head;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 数据出队消费(仅仅支持单线程消费)
     *
     * @param consumer
     * @throws InterruptedException
     */
    @Override
    public void consumeHead(Consumer<NodeRecord<T>> consumer) throws InterruptedException {
        if (cacheMeta.headRecordId == null) {
            return;
        } else {
            //获取头部(存在尾部指针脏读情况)
            NodeRecord<T> head = getHead();
            if (head == null) {
                return;
            }
            try {
                //进行消费
                consumer.accept(head);
            } catch (Exception ex) {
                throw ex;
            }
            //获取锁
            lock.lockInterruptibly();
            try {
                //再次获取头部, 规避第一次获取头部时存在的脏读.
                head = getHead();
                //出队处理
                dequeue(head);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 数据批量出队消费(仅仅支持单线程消费)
     *
     * @param consumer
     * @throws InterruptedException
     */
    @Override
    public void consumeHeads(Consumer<List<NodeRecord<T>>> consumer) throws InterruptedException {
        consumeHeads(consumer, 1024 * 16);
    }

    /**
     * 数据出队消费(仅仅支持单线程消费)
     *
     * @param consumer
     * @param consumeSize
     * @throws Exception
     */
    @Override
    public void consumeHeads(Consumer<List<NodeRecord<T>>> consumer, long consumeSize) throws InterruptedException {
        if (cacheMeta.headRecordId == null) {
            return;
        } else {
            //读取批量消费数据.
            LinkedList<NodeRecord<T>> linkedRecords = foreach(consumeSize);
            if (linkedRecords.size() == 0) {
                return;
            }
            NodeRecord<T> head = linkedRecords.getLast();
            try {
                //批量消费.
                consumer.accept(linkedRecords);
            } catch (Exception ex) {
                throw ex;
            }
            //消费成功可以移除指针.
            lock.lockInterruptibly();
            try {
                //再次获取最后一个节点, 存在的脏读.
                NodeRecord<T> head$ = elementRecord(head.getId());
                linkedRecords.removeLast();
                linkedRecords.addLast(head$);
                //出队处理
                dequeue(linkedRecords);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 数据两段式出队消费(仅仅支持单线程消费)
     *
     * @param consumer 消费者
     * @return 返回提交回调
     * @throws Exception
     */
    @Override
    public CacheQueue.CommitCallback consumeHeadsAndAsyncCommit(Consumer<List<NodeRecord<T>>> consumer) {
        return consumeHeadsAndAsyncCommit(consumer, 1024 * 16);
    }

    /**
     * 数据两段式出队消费(仅仅支持单线程消费)
     *
     * @param consumer    消费者
     * @param consumeSize 消费数量
     * @return 返回提交回调
     * @throws Exception
     */
    @Override
    public CacheQueue.CommitCallback consumeHeadsAndAsyncCommit(Consumer<List<NodeRecord<T>>> consumer, long consumeSize) {
        if (cacheMeta.headRecordId == null) {
            return NO_CONSUME_COMMIT_CALLBACK;
        } else {
            //读取批量消费数据.
            LinkedList<NodeRecord<T>> linkedRecords = foreach(consumeSize);
            //无可消费
            if (linkedRecords.size() == 0) {
                return NO_CONSUME_COMMIT_CALLBACK;
            }
            try {
                //批量消费.
                consumer.accept(linkedRecords);
            } catch (Exception ex) {
                throw ex;
            }
            //返回提交回调.
            return (committed) -> {
                //提交完毕, 数据出队更新.
                if (committed) {
                    try {
                        lock.lockInterruptibly();
                        //再次获取最后一个节点, 存在的脏读.
                        NodeRecord<T> head$ = elementRecord(linkedRecords.getLast().getId());
                        linkedRecords.removeLast();
                        linkedRecords.addLast(head$);
                        //出队处理.
                        dequeue(linkedRecords);
                    } finally {
                        lock.unlock();
                        linkedRecords.clear();
                    }
                } else {
                    //销毁在内存因未提交而贮存的数据,
                    linkedRecords.clear();
                }
            };
        }
    }

    /**
     * 数据出队(无数据阻塞)
     *
     * @return
     * @throws InterruptedException
     */
    public NodeRecord<T> takeHead() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (cacheMeta.headRecordId == null) {
                notEmpty.await();
            }
            NodeRecord<T> head = getHead();
            dequeue(head);
            return head;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 按size大小遍历,并按顺序放到链表里.
     *
     * @param size
     * @return 顺序遍历的链表中元素
     */
    private LinkedList<NodeRecord<T>> foreach(long size) {
        LinkedList<NodeRecord<T>> linkedRecords = new LinkedList<>();
        if (cacheMeta.headRecordId == null) {
            return linkedRecords;
        }
        //获取当前链表的头ID.
        String currentRecordId = cacheMeta.headRecordId;
        NodeRecord<T> head;
        int consumeCount = 0;
        //循环遍历链表.
        do {
            head = elementRecord(currentRecordId);
            if (head != null) {
                linkedRecords.add(head);
                currentRecordId = head.getNextId();
                consumeCount++;
            } else {
                currentRecordId = null;
            }
            //判断是否到达遍历的限制大小.
            if (size > 0 && consumeCount >= size) {
                break;
            }
        } while (currentRecordId != null);
        return linkedRecords;
    }

    /**
     * 出队
     *
     * @param head
     */
    private void dequeue(NodeRecord<T> head) {
        if (head == null || !head.getId().equals(cacheMeta.headRecordId))
            return;
        if (head.getNextId() == null) {
            cacheMeta.tailRecordId = cacheMeta.headRecordId = null;
        } else {
            cacheMeta.headRecordId = head.getNextId();
        }
        WriteBatch writeBatch = cacheStore.createWriteBatch();
        KV<byte[], byte[]> metaKV = cacheMeta.toBytes();
        writeBatch.put(metaKV.key(), metaKV.value());
        writeBatch.delete(SerializeHelper.serialize(head.getId()));
        cacheStore.writeSync(writeBatch);
    }

    private void dequeue(LinkedList<NodeRecord<T>> linkedRecords) {
        if (linkedRecords.size() == 0 || !linkedRecords.getFirst().getId().equals(cacheMeta.headRecordId)) {
            return;
        }
        NodeRecord<T> head = linkedRecords.getLast();
        if (head.getNextId() == null) {
            cacheMeta.tailRecordId = cacheMeta.headRecordId = null;
        } else {
            cacheMeta.headRecordId = head.getNextId();
        }
        WriteBatch writeBatch = cacheStore.createWriteBatch();
        KV<byte[], byte[]> metaKV = cacheMeta.toBytes();
        writeBatch.put(metaKV.key(), metaKV.value());
        for (NodeRecord<T> record : linkedRecords) {
            writeBatch.delete(SerializeHelper.serialize(record.getId()));
        }
        cacheStore.writeSync(writeBatch);
    }

    private NodeRecord<T> elementRecord(String recordId) {
        byte[] data = cacheStore.get(SerializeHelper.serialize(recordId));
        if (data != null) {
            return SerializeHelper.deserialize(data, NodeRecord.class);
        } else {
            return null;
        }
    }

    /**
     * 获取头数据
     *
     * @return
     */
    public NodeRecord<T> getHead() {
        return elementRecord(cacheMeta.headRecordId);
    }

    /**
     * 获取尾部数据
     *
     * @return
     */
    public NodeRecord<T> getTail() {
        return elementRecord(cacheMeta.tailRecordId);
    }

    public boolean available() {
        return cacheMeta.headRecordId != null;
    }

    @Override
    public CacheMeta getCacheMeta() {
        return cacheMeta;
    }

    /**
     * 捕获逃逸数据,并插入队尾
     *
     * @throws Exception
     */
    @Override
    public void catchEscape() {
        //队列所有数据ID
        Set<String> recordIdsSet = new HashSet<>();
        //逃逸数据ID列表
        List<String> catchEscapeRecordIds = new ArrayList<>();
        try {
            lock.lock();
            //获取队列数据id列表
            String rid = cacheMeta.headRecordId;
            do {
                NodeRecord<T> record = elementRecord(rid);
                if (record != null) {
                    recordIdsSet.add(record.getId());
                    rid = record.getNextId();
                } else {
                    rid = null;
                }
            } while (!MixAll.isNullOrEmpty(rid));

            //获取簇全部数据
            cacheStore.foreach(new LevelDB.Foreach() {
                @Override
                public void foreach(byte[] key, byte[] value) {
                    String recordId = SerializeHelper.deserialize(key, String.class);
                    if (recordIdsSet.contains(recordId) || recordId.equals(cacheMeta.cacheId)) {
                        return;
                    } else {
                        //逃逸数据
                        NodeRecord<T> record = SerializeHelper.deserialize(value, NodeRecord.class);
                        try {
                            putTail(record.fresh());
                            catchEscapeRecordIds.add(recordId);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
            if (catchEscapeRecordIds.isEmpty()) {
                return;
            } else {
                WriteBatch writeBatch = cacheStore.createWriteBatch();
                for (String catchEscapeRecordId : catchEscapeRecordIds) {
                    writeBatch.delete(SerializeHelper.serialize(catchEscapeRecordId));
                }
                cacheStore.writeSync(writeBatch);
            }
        } catch (Exception ex) {
            LOGGER.error("LinkedCacheQueue {} catch escape data failed.", cacheQueueId, ex);
        } finally {
            catchEscapeRecordIds.clear();
            recordIdsSet.clear();
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        cacheStore.close();
    }
}
