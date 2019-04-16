package com.stereo.study.proxy.test;


import com.stereo.study.proxy.IProxyInstance;

/**
 * Created by liuj-ai on 2018/4/13.
 */
public interface ITestInterface extends IProxyInstance {

    Integer helloworld(String helloworld) throws Exception;
}
