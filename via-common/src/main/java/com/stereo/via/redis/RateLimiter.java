package com.stereo.via.redis;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import org.springframework.scripting.support.ResourceScriptSource;
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
//    private final long period;
//    private final int limited;
//    private RedisTemplate<String, Object> redisTemplate;
//    public RateLimiter(int limited, long period, RedisTemplate redisTemplate)
//    {
//        this.limited = limited;
//        this.period = period;
//        this.redisTemplate = redisTemplate;
//        setScriptSource(new ResourceScriptSource(new ClassPathResource("rateLimiter.lua")));
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