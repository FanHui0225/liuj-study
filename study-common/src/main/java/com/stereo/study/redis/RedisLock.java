package com.stereo.study.redis;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.util.Arrays;
//import java.util.UUID;
//
///**
// * 全局锁
// * @See lua解决死锁
// * @See 不可重入锁(待解决)
// *
// * Created by liuj-ai on 2017/6/22.
// */
//public final class RedisLock {
//
//    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);
//    private static final int DEFAULT_EXPIRE = 60 * 1000;
//    private RedisTemplate<String,String> template;
//    private final String lockKey;
//    private final String primaryValue;
//    private volatile boolean locked = false;
//    private static DefaultRedisScript<Long> lockLuaScript;
//    private static DefaultRedisScript<Long> unLockLuaScript;
//    private transient Thread ownerThread;
//
//    static
//    {
//        lockLuaScript = new DefaultRedisScript<Long>()
//        {
//            {
//                String lockLuaScriptString = "local key = KEYS[1]\n" +
//                        "local ttl = ARGV[1]\n" +
//                        "local value = ARGV[2]\n" +
//                        "local lockSet = redis.call('setnx', key, value)\n" +
//                        "if lockSet == 1 then\n" +
//                        "\tredis.call('pexpire', key, ttl)\n" +
//                        "end\n" +
//                        "return lockSet";
//                setScriptText(lockLuaScriptString);
//                setResultType(Long.class);
//            }
//        };
//        unLockLuaScript = new DefaultRedisScript<Long>()
//        {
//            {
//                String unLockLuaScriptString =
//                        "if redis.call('get', KEYS[1]) == ARGV[1]\n" +
//                        "then\n" +
//                        "\treturn redis.call('del', KEYS[1])\n" +
//                        "else\n" +
//                        "\treturn 0\n" +
//                        "end";
//                setScriptText(unLockLuaScriptString);
//                setResultType(Long.class);
//            }
//        };
//    }
//
//    public RedisLock(RedisConnectionFactory redisConnectionFactory, String lockKey)
//    {
//        this(redisConnectionFactory,lockKey,UUID.randomUUID().toString());
//    }
//
//    public RedisLock(RedisConnectionFactory redisConnectionFactory, String lockKey, String primaryValue)
//    {
//        this.template = new RedisTemplate<String, String>();
//        template.setConnectionFactory(redisConnectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//        this.lockKey = lockKey + "_lock";
//        this.primaryValue = primaryValue;
//    }
//
//    public RedisLock(RedisTemplate<String, String> redisTemplate, String lockKey)
//    {
//        this(redisTemplate,lockKey, UUID.randomUUID().toString());
//    }
//
//    public RedisLock(RedisTemplate<String, String> redisTemplate, String lockKey, String primaryValue)
//    {
//        this.template = redisTemplate;
//        this.lockKey = lockKey + "_lock";
//        this.primaryValue = primaryValue;
//    }
//
//    public String getLockKey() {
//        return lockKey;
//    }
//
//    public synchronized boolean tryLock(long ttl) throws InterruptedException
//    {
//        //final Thread current = Thread.currentThread();
//        //setOwnerThread(current)
//        ttl = ttl <= 0 ? DEFAULT_EXPIRE : ttl;
//        Long tryLockFlag = template.execute(
//            lockLuaScript,
//            Arrays.asList(lockKey),
//            String.valueOf(ttl),
//            String.valueOf(primaryValue)
//        );
//        boolean tryLock = tryLockFlag == 0 ? false : true;
//        if (tryLock)
//            locked = true;
//        return tryLock;
//    }
//
//    public synchronized void unLock()
//    {
//        if (locked)
//        {
//           template.execute(unLockLuaScript, Arrays.asList(lockKey),String.valueOf(primaryValue));
//            locked = false;
//            //setOwnerThread(null);
//        }
//    }
//
//
//    protected final void setOwnerThread(Thread thread) {
//        ownerThread = thread;
//    }
//
//    protected final Thread getOwnerThread() {
//        return ownerThread;
//    }
//}