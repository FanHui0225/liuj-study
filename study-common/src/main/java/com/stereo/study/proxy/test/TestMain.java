package com.stereo.study.proxy.test;


import com.stereo.study.proxy.ProxyExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuj-ai on 2018/4/13.
 */
public class TestMain {
    private final static Logger logger = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws Exception {
        //代理实现
        TestProxyInstance testProxyInstance = new TestProxyInstance();
        //代理执行器
        ProxyExecutor<ITestInterface> proxyExecutor = new ProxyExecutor<>();
        //打开代理
        final ITestInterface testInterface = proxyExecutor.open(ITestInterface.class, testProxyInstance);

        //并发调用helloworld方法
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        final List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //执行代理方法
                        list.add(testInterface.helloworld("你好"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //关闭代理
        proxyExecutor.close();
        //关闭线程池
        executorService.shutdown();

        //输出1 -> 100 并发执行成功
        logger.info("输出helloworld方法并发下的执行结果 list => {}", list);
    }
}
