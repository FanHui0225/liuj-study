package com.stereo.study.levedbqueue;

/**
 * Created by liuj-ai on 2020/3/14.
 */
public class CacheStore extends LevelDB {

    public CacheStore() {
        super(new CacheConfig());
    }

    public CacheStore(String localPath) {
        this(localPath, true);
    }

    public CacheStore(CacheConfig cacheConfig) {
        super(cacheConfig);
    }

    public CacheStore(String localPath, boolean storePathPreClean) {
        super(new CacheConfig(localPath, storePathPreClean));
    }
}
