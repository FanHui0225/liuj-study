package com.stereo.study.levedbqueue.demo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by liuj-ai on 2020/3/16.
 */
public class User implements Serializable {

    private Map<String, Test> test1;
    private List<Test> test2;
    private List<String> test4;
    private String test3;
    private Integer count;

    public Map<String, Test> getTest1() {
        return test1;
    }

    public void setTest1(Map<String, Test> test1) {
        this.test1 = test1;
    }

    public List<Test> getTest2() {
        return test2;
    }

    public void setTest2(List<Test> test2) {
        this.test2 = test2;
    }

    public List<String> getTest4() {
        return test4;
    }

    public void setTest4(List<String> test4) {
        this.test4 = test4;
    }

    public String getTest3() {
        return test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "User{" +
                "test1=" + test1 +
                ", test2=" + test2 +
                ", test4=" + test4 +
                ", test3='" + test3 + '\'' +
                ", count=" + count +
                '}';
    }
}
