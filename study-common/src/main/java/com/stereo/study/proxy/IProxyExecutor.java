package com.stereo.study.proxy;



/**
 * Created by liuj-ai on 2018/4/12.
 */
public interface IProxyExecutor<T extends IProxyInstance> {

    T open(Class<T> t, IProxyInstance instance) throws InterruptedException;

    void close();
}
