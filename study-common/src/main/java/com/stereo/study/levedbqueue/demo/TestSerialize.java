package com.stereo.study.levedbqueue.demo;

import com.stereo.study.levedbqueue.NodeRecord;
import com.stereo.study.util.SerializeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuj-ai on 2020/3/16.
 */
public class TestSerialize {


    public static void main(String[] args) {
        User user = user();
        System.out.println(user);
        NodeRecord<User> record = new NodeRecord<>(user);
        byte[] data = SerializeHelper.serialize(record);
        record = SerializeHelper.deserialize(data, NodeRecord.class);
        System.out.println(record.getRecord());
    }

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
        return user;
    }
}
