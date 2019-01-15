package com.stereo.via.bytecode.test;

import com.stereo.via.bytecode.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuj-ai on 2019/1/15.
 */
public class TestMain {

    public static void main(String[] args) throws Exception {
        while (true) {
            long start = System.currentTimeMillis();
            TestInterface testInterface = (TestInterface) java.lang.reflect.Proxy.newProxyInstance(TestMain.class.getClassLoader(), new Class<?>[] {TestInterface.class, TestInterface2.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return "哈哈_1";
                }
            });
            testInterface.testStr();
            System.out.println("jdk 耗时: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            TestInterface2 testInterface2 = (TestInterface2) Proxy.getProxy(TestInterface.class, TestInterface2.class).newInstance(new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return "哈哈_2";
                }
            });
            testInterface2.testStr2();
            System.out.println("javassist 耗时: " + (System.currentTimeMillis() - start));
            Thread.sleep(1000L);
        }
    }
}
