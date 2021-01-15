package com.stereo.study.levedbqueue;


import com.stereo.study.util.KV;
import com.stereo.study.util.SerializeHelper;
import com.stereo.study.util.SystemClock;

import java.io.Serializable;

/**
 * Created by liuj-ai on 2020/3/14.
 */
public final class CacheMeta implements Serializable {

    //存储记录链表尾部记录ID
    volatile String tailRecordId;
    //存储记录链表头部记录ID
    volatile String headRecordId;
    //缓存ID
    final String cacheId;
    //创建时间
    long createTime;

    protected void load(CacheStore cacheStore) {
        MetaSnapshot metaSnapshot;
        //读取元信息
        byte[] metaData = cacheStore.get(SerializeHelper.serialize(cacheId));
        if (metaData != null) {
            metaSnapshot = SerializeHelper.deserialize(metaData, MetaSnapshot.class);
        } else {
            metaSnapshot = null;
        }
        if (metaSnapshot != null) {
            //信息装入内存
            tailRecordId = metaSnapshot.tailRecordId;
            headRecordId = metaSnapshot.headRecordId;
            createTime = metaSnapshot.createTime;
        } else {
            createTime = SystemClock.now();
        }
    }

    public CacheMeta(String cacheId) {
        this.cacheId = cacheId;
    }

    KV<byte[], byte[]> toBytes() {
        MetaSnapshot metaSnapshot = new MetaSnapshot(tailRecordId, headRecordId, cacheId, createTime);
        return SerializeHelper.kv(cacheId, metaSnapshot);
    }

    public String getTailRecordId() {
        return tailRecordId;
    }

    public void setTailRecordId(String tailRecordId) {
        this.tailRecordId = tailRecordId;
    }

    public String getHeadRecordId() {
        return headRecordId;
    }

    public void setHeadRecordId(String headRecordId) {
        this.headRecordId = headRecordId;
    }

    public String getCacheId() {
        return cacheId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public static class MetaSnapshot implements Serializable {
        private String tailRecordId;
        private String headRecordId;
        private String cacheId;
        private long createTime;

        public MetaSnapshot() {
        }

        public MetaSnapshot(String tailRecordId, String headRecordId, String cacheId, long createTime) {
            this.tailRecordId = tailRecordId;
            this.headRecordId = headRecordId;
            this.cacheId = cacheId;
            this.createTime = createTime;
        }

        public String getTailRecordId() {
            return tailRecordId;
        }

        public void setTailRecordId(String tailRecordId) {
            this.tailRecordId = tailRecordId;
        }

        public String getHeadRecordId() {
            return headRecordId;
        }

        public void setHeadRecordId(String headRecordId) {
            this.headRecordId = headRecordId;
        }

        public String getCacheId() {
            return cacheId;
        }

        public void setCacheId(String cacheId) {
            this.cacheId = cacheId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
