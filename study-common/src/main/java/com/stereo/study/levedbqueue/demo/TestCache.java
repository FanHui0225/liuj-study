package com.stereo.study.levedbqueue.demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.stereo.study.levedbqueue.CacheQueue;
import com.stereo.study.levedbqueue.CacheStore;
import com.stereo.study.levedbqueue.LinkedCacheQueue;
import com.stereo.study.levedbqueue.NodeRecord;
import com.stereo.study.util.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 缓存队列测试
 * <p>
 * Created by liuj-ai on 2020/3/16.
 */
public class TestCache {

    public static void main(String[] args) throws Exception {
        CacheQueue<NodeRecord<String>> cacheQueue = new LinkedCacheQueue<>("test-cache-id", new CacheStore("D:\\workspace\\cache\\test", true), false);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        //生产者
        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 5000; j++) {
                    try {
                        long now = SystemClock.now();
                        cacheQueue.putTail(record());
                        System.out.println("生产线程 -> " + Thread.currentThread().getName() + " 写入数据开销: " + (SystemClock.now() - now));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //消费者
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger(0);
            while (true) {
                try {
                    cacheQueue.consumeHeads(new Consumer<List<NodeRecord<String>>>() {
                        @Override
                        public void accept(List<NodeRecord<String>> nodeRecords) {
                            try {
                                for (NodeRecord<String> u : nodeRecords) {
                                    //System.out.println("消费线程 -> " + Thread.currentThread().getName() + " 消费数据: " + u.getRecord() + "   已消费数据: " + count.incrementAndGet());
                                    System.out.println("消费线程 -> " + Thread.currentThread().getName() + "   已消费数据: " + count.incrementAndGet());
                                }
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static NodeRecord<String> record() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = user();
            users.add(user);
        }
        String json = JSON.toJSONString(users, SerializerFeature.IgnoreNonFieldGetter);
        return NodeRecord.record(json);
    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    private static User user() {
        User user = new User();
        Test test = new Test();
        test.setTest("0");

        Test test1 = new Test();
        test1.setTest("1");

        Map<String, Test> testMap = new HashMap<>();
        testMap.put("0", test);
        user.setTest1(testMap);

        List<Test> testList = new ArrayList<>();
        testList.add(test1);
        user.setTest2(testList);

        List<String> stringList = new ArrayList<>();
        stringList.add("test----string----list");
        user.setTest4(stringList);

        user.setTest3("testUser");
        user.setCount(counter.getAndIncrement());
        return user;
    }
}
