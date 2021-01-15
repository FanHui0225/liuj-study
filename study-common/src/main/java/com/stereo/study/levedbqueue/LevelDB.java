package com.stereo.study.levedbqueue;

import com.stereo.study.util.MixAll;
import com.stereo.study.util.SystemClock;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.fusesource.leveldbjni.JniDBFactory.asString;

/**
 * 基于LevelDB做本地持久化存储
 * <p>
 * Created by liuj-ai on 2020/10/27.
 */
public class LevelDB {
    private static final Logger log = LoggerFactory.getLogger(LevelDB.class);
    private final CacheConfig cacheConfig;
    private final DBFactory factory = JniDBFactory.factory;
    private final Options DB_OPTIONS = new Options();
    private final ReadOptions READ_OPTIONS = new ReadOptions();
    private final WriteOptions WRITE_OPTIONS_SYNC = new WriteOptions();
    private final WriteOptions WRITE_OPTIONS_ASYNC = new WriteOptions();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private DB DB;

    public interface Foreach {
        void foreach(byte[] key, byte[] value);
    }

    public LevelDB(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public void init() {
        DB_OPTIONS
                .createIfMissing(true)
                .blockSize(cacheConfig.getBlockSize())
                .blockRestartInterval(cacheConfig.getBlockRestartInterval())
                .writeBufferSize(cacheConfig.getWriteBufferSize())
                .cacheSize(cacheConfig.getCacheSize())
                .maxOpenFiles(cacheConfig.getMaxOpenFiles())
                .compressionType(cacheConfig.getCompressionType())
                .paranoidChecks(cacheConfig.isParanoidChecks())
                .verifyChecksums(cacheConfig.isVerifyChecksums())
                .logger(cacheConfig.getLogger())
                .comparator(cacheConfig.getComparator());
        WRITE_OPTIONS_SYNC.sync(true);
        WRITE_OPTIONS_ASYNC.sync(false);
        READ_OPTIONS.verifyChecksums(cacheConfig.isVerifyChecksums());
        READ_OPTIONS.fillCache(cacheConfig.isReadFillCache());
        long start = SystemClock.now();
        boolean result = MixAll.createIfNotExistsDir(new File(this.cacheConfig.getLevelDbPath()));
        assert result;
        try {
            DB = factory.open(new File(cacheConfig.getLevelDbPath()), DB_OPTIONS);
            scheduledExecutorService.scheduleWithFixedDelay(new TimerTask() {
                @Override
                public void run() {
                    compact();
                }
            }, cacheConfig.getCompactInterval(), cacheConfig.getCompactInterval(), TimeUnit.MILLISECONDS);
            long end = SystemClock.now();
            log.info("[LevelDB] -> start LevelDB success, consumeTime:{}", (end - start));
        } catch (IOException e) {
            log.error("[LevelDB] -> init LevelDB error, ex:{}", e);
            System.exit(-1);
        }
    }

    private final void compact() {
        try {
            DB.compactRange(null, null);
            String stats = DB.getProperty("leveldb.stats");
            log.info("[LevelDB] -> LevelDB compact success, stats:{}", stats);
        } catch (DBException ex) {
            log.error("[LevelDB] -> LevelDB compact error, ex:{}", ex);
        }
    }

    public WriteBatch createWriteBatch() {
        return DB.createWriteBatch();
    }

    public void writeSync(WriteBatch writeBatch) {
        try {
            DB.write(writeBatch, WRITE_OPTIONS_SYNC);
        } finally {
            try {
                writeBatch.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> writeSync LevelDB writeBatch close error, ex:{}", e);
            }
        }
    }

    public void writeAsync(WriteBatch writeBatch) {
        try {
            DB.write(writeBatch, WRITE_OPTIONS_ASYNC);
        } finally {
            try {
                writeBatch.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> writeAsync LevelDB writeBatch close error, ex:{}", e);
            }
        }
    }

    public boolean write(final WriteBatch writeBatch, final WriteOptions writeOptions) {
        try {
            this.DB.write(writeBatch, writeOptions);
        } catch (DBException e) {
            log.error("[LevelDB] -> error while write batch, err:{}", e.getMessage(), e);
            return false;
        } finally {
            try {
                writeBatch.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> write LevelDB writeBatch close error, ex:{}", e);
            }
        }
        return true;
    }

    public Snapshot putAsync(final byte[] key, final byte[] value) {
        return this.DB.put(key, value, WRITE_OPTIONS_ASYNC);
    }

    public Snapshot putSync(final byte[] key, final byte[] value) {
        return this.DB.put(key, value, WRITE_OPTIONS_SYNC);
    }

    public boolean put(final byte[] key, final byte[] value, final WriteOptions writeOptions) {
        try {
            this.DB.put(key, value, writeOptions);
        } catch (DBException e) {
            log.error("[LevelDB] -> error while put, key:{}, err:{}",
                    asString(key), e.getMessage(), e);
            return false;
        }
        return true;
    }

    public DBIterator newIterator() {
        return DB.iterator(READ_OPTIONS);
    }

    public DBIterator newIterator(final ReadOptions options) {
        return DB.iterator(options);
    }

    private void foreach(final DBIterator iterator, final Foreach foreach) {
        try {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                Map.Entry<byte[], byte[]> entry = iterator.peekNext();
                foreach.foreach(entry.getKey(), entry.getValue());
            }
        } finally {
            try {
                iterator.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> foreach LevelDB iterator close error, ex:{}", e);
            }
        }
    }

    public void foreach(final Foreach foreach) {
        foreach(newIterator(), foreach);
    }

    public void foreach(final Foreach foreach, final ReadOptions options) {
        foreach(newIterator(options), foreach);
    }

    public byte[] get(final byte[] key) {
        try {
            return DB.get(key, READ_OPTIONS);
        } catch (DBException e) {
            log.error("[LevelDB] -> error while get, key:{}, err:{}",
                    asString(key), e.getMessage(), e);
            return null;
        }
    }

    public byte[] get(final byte[] key, final ReadOptions options) {
        try {
            return DB.get(key, options);
        } catch (DBException e) {
            log.error("[LevelDB] -> error while get, key:{}, err:{}",
                    asString(key), e.getMessage(), e);
            return null;
        }
    }

    public List<byte[]> getByPrefix(final byte[] prefixKey) {
        List<byte[]> values = new ArrayList<>();
        DBIterator iterator = newIterator();
        try {
            for (iterator.seek(prefixKey); iterator.hasNext(); iterator.next()) {
                Map.Entry<byte[], byte[]> entry = iterator.peekNext();
                if (asString(entry.getKey()).startsWith(asString(prefixKey))) {
                    values.add(entry.getValue());
                }
            }
        } catch (Exception e) {
            log.error("[LevelDB] ->  error while get by prefix, prefixKey:{}, err:{}",
                    asString(prefixKey), e);
        } finally {
            try {
                iterator.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> getByPrefix LevelDB iterator close error, ex:{}", e);
            }
        }
        return values;
    }

    public List<byte[]> pollByPrefix(final byte[] prefixKey, final int nums) {
        List<byte[]> values = new ArrayList<>();
        int count = 0;
        DBIterator iterator = newIterator();
        WriteBatch writeBatch = DB.createWriteBatch();
        try {
            for (iterator.seek(prefixKey); iterator.hasNext(); iterator.next()) {
                Map.Entry<byte[], byte[]> entry = iterator.peekNext();
                if (asString(entry.getKey()).startsWith(asString(prefixKey))) {
                    values.add(entry.getValue());
                    writeBatch.delete(entry.getKey());
                    count++;
                }
                if (count >= nums) {
                    break;
                }
            }
            if (count > 0) {
                this.DB.write(writeBatch, WRITE_OPTIONS_SYNC);
            }
        } catch (DBException e) {
            log.error("[LevelDB] ->  error while get by prefix, pollByPrefix:{}, err:{}",
                    asString(prefixKey), e);
        } finally {
            try {
                writeBatch.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> pollByPrefix LevelDB writeBatch close error, ex:{}", e);
            }
            try {
                iterator.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> pollByPrefix LevelDB iterator close error, ex:{}", e);
            }
        }
        return values;
    }

    public int getCountByPrefix(final byte[] prefixKey) {
        int count = 0;
        DBIterator iterator = newIterator();
        try {
            for (iterator.seek(prefixKey); iterator.hasNext(); iterator.next()) {
                Map.Entry<byte[], byte[]> entry = iterator.peekNext();
                if (asString(entry.getKey()).startsWith(asString(prefixKey))) {
                    count++;
                }
            }
        } catch (Exception e) {
            log.error("[LevelDB] ->  error while get count by prefix, prefixKey:{}, err:{}",
                    asString(prefixKey), e);
        } finally {
            try {
                iterator.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> getCountByPrefix LevelDB iterator close error, ex:{}", e);
            }
        }
        return count;
    }

    public boolean deleteByPrefix(final byte[] prefixKey, boolean sync) {
        WriteBatch writeBatch = createWriteBatch();
        DBIterator iterator = newIterator();
        try {
            int item = 0;
            for (iterator.seek(prefixKey); iterator.hasNext(); iterator.next()) {
                Map.Entry<byte[], byte[]> entry = iterator.peekNext();
                if (asString(entry.getKey()).startsWith(asString(prefixKey))) {
                    writeBatch.delete(entry.getKey());
                    item++;
                }
            }
            if (item > 0) {
                this.DB.write(writeBatch, sync ? WRITE_OPTIONS_SYNC : WRITE_OPTIONS_ASYNC);
            }
        } catch (DBException e) {
            log.error("[LevelDB] ->  error while delete by prefix, prefixKey:{}, err:{}",
                    asString(prefixKey), e);
            return false;
        } finally {
            try {
                iterator.close();
            } catch (IOException e) {
                log.error("[LevelDB] -> deleteByPrefix LevelDB iterator close error, ex:{}", e);
            }
        }
        return true;
    }

    public boolean delete(final byte[] key) {
        try {
            this.DB.delete(key);
        } catch (DBException e) {
            log.error("[LevelDB] -> while delete key, key:{}, err:{}",
                    asString(key), e.getMessage(), e);
        }
        return true;
    }

    public void close() {
        scheduledExecutorService.shutdown();
        if (DB != null) {
            try {
                DB.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("[LevelDB] -> LevelDB close error, ex:{}", e);
            }
        }
    }
}
