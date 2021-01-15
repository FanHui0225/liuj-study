package com.stereo.study.levedbqueue;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.Logger;

import static com.stereo.study.util.CacheUtils.buildCacheStoreFolder;


/**
 * Created by liuj-ai on 2019/11/4.
 */
public final class CacheConfig {
    private String levelDbPath;
    private int writeBufferSize = 4 << 20;
    private int maxOpenFiles = 1000;
    private int blockRestartInterval = 16;
    private int blockSize = 32 * 1024;
    private CompressionType compressionType = CompressionType.SNAPPY;
    private boolean verifyChecksums = true;
    private boolean paranoidChecks = false;
    private DBComparator comparator = null;
    private Logger logger = null;
    private long cacheSize;
    private boolean readFillCache = true;
    private long compactInterval = 3600000L;

    public CacheConfig() {
        this.levelDbPath = buildCacheStoreFolder(null);
    }

    public CacheConfig(String rocksDbPath, boolean preClean) {
        this.levelDbPath = buildCacheStoreFolder(rocksDbPath, preClean);
    }

    public boolean isReadFillCache() {
        return readFillCache;
    }

    public void setReadFillCache(boolean readFillCache) {
        this.readFillCache = readFillCache;
    }

    public String getLevelDbPath() {
        return levelDbPath;
    }

    public void setLevelDbPath(String levelDbPath) {
        this.levelDbPath = levelDbPath;
    }

    public int getBlockRestartInterval() {
        return blockRestartInterval;
    }

    public void setBlockRestartInterval(int blockRestartInterval) {
        this.blockRestartInterval = blockRestartInterval;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    public boolean isVerifyChecksums() {
        return verifyChecksums;
    }

    public void setVerifyChecksums(boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
    }

    public boolean isParanoidChecks() {
        return paranoidChecks;
    }

    public void setParanoidChecks(boolean paranoidChecks) {
        this.paranoidChecks = paranoidChecks;
    }

    public DBComparator getComparator() {
        return comparator;
    }

    public void setComparator(DBComparator comparator) {
        this.comparator = comparator;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getMaxOpenFiles() {
        return maxOpenFiles;
    }

    public void setMaxOpenFiles(int maxOpenFiles) {
        this.maxOpenFiles = maxOpenFiles;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }


    public long getCompactInterval() {
        return compactInterval;
    }

    public void setCompactInterval(long compactInterval) {
        this.compactInterval = compactInterval;
    }
}
