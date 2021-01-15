package com.stereo.study.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuj-ai on 2020/3/16.
 */
public class SerializeHelper {
    private static final Logger log = LoggerFactory.getLogger(SerializeHelper.class);

    public static <K, V> KV kv(K k, V v) {
        return new KV<byte[], byte[]>() {
            @Override
            public byte[] key() {
                return SerializeHelper.serialize(k);
            }

            @Override
            public byte[] value() {
                return SerializeHelper.serialize(v);
            }
        };
    }

    public static <T> byte[] serialize(T obj) {
        if (obj instanceof String) {
            return ((String) obj).getBytes();
        }
        return JSONObject.toJSONBytes(obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            if (clazz == String.class) {
                return (T) new String(bytes);
            }
            return JSONObject.parseObject(bytes, clazz, Feature.DisableSpecialKeyDetect);
        } catch (Exception ex) {
            log.warn("Deserialize failure,cause={}", ex);
        }
        return null;
    }

    /*
    public static <T> List<T> deserializeList(byte[] bytes, Class<T> clazz) {
        try {
            String json = JSONObject.toJSONString(bytes);
            List<T> result = JSONObject.parseArray(json, clazz);
            return result;
        } catch (Exception ex) {
            log.warn("Deserialize failure,cause={}", ex);
        }
        return null;
    }
    */
}
