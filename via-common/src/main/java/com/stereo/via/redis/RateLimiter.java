package com.stereo.via.redis;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import java.util.Arrays;
//
///**
// * redis lua 限流器
// * Created by liuj-ai on 2017/11/9.
// */
//public final class RateLimiter extends DefaultRedisScript<Long>
//{
//    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
//
//    private static final String rateLimiterLuaScript =
//            "local key = \"rate_limit_\" .. KEYS[1]\n" +
//                    "local limit = tonumber(ARGV[1])\n" +
//                    "local expire_time = ARGV[2]\n" +
//                    "local is_exists = redis.call(\"EXISTS\", key)\n" +
//                    "if is_exists == 1 then\n" +
//                    "    if redis.call(\"INCR\", key) > limit then\n" +
//                    "        return 0\n" +
//                    "    else\n" +
//                    "        return 1\n" +
//                    "    end\n" +
//                    "else\n" +
//                    "    redis.call(\"SET\", key, 1)\n" +
//                    "    redis.call(\"EXPIRE\", key, expire_time)\n" +
//                    "    return 1\n" +
//                    "end";
//
//    private final long period;
//    private final int limited;
//    private RedisTemplate<String, String> redisTemplate;
//    public RateLimiter(int limited, long period, RedisTemplate<String, String> redisTemplate)
//    {
//        this.limited = limited;
//        this.period = period;
//        this.redisTemplate = redisTemplate;
//        //setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/rateLimiter.lua")));
//        setScriptText(rateLimiterLuaScript);
//        setResultType(Long.class);
//    }
//
//    /**
//     * 试图获取令牌
//     * @param limitkey
//     * @return
//     */
//    public boolean acquire(String limitkey)
//    {
//        try
//        {
//            Long acquireFlag = redisTemplate.execute(
//                    this,
//                    Arrays.asList(limitkey),
//                    String.valueOf(limited),
//                    String.valueOf(period)
//            );
//            return acquireFlag == 0 ? false : true;
//        }catch (Exception ex)
//        {
//            logger.error("RateLimiter 获取令牌桶失败 limitkey=" + limitkey , ex);
//            return false;
//        }
//    }
//}
