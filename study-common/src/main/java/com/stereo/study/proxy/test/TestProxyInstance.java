package com.stereo.study.proxy.test;

/**
 * Created by liuj-ai on 2018/4/13.
 */
public class TestProxyInstance implements ITestInterface {

    private volatile int i;
    @Override
    public void opened() {
        System.out.println("TestProxyInstance.opened");
    }

    @Override
    public void closed() {
        System.out.println("TestProxyInstance.closed");
    }

    @Override
    public Integer helloworld(String helloworld) throws Exception {
        i++;
        return i;
    }
}
