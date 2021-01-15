package com.stereo.study.levedbqueue.demo;

import java.io.Serializable;

/**
 * Created by liuj-ai on 2020/3/16.
 */
public class Test implements Serializable{

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "Test{" +
                "test='" + test + '\'' +
                '}';
    }
}
