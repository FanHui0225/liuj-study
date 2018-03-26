package com.stereo.via.bytecode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuj-ai on 2018/3/26.
 */
public class TestMain {

    public static void main(String[] args) throws Exception {

        TestInterface testInterface = (TestInterface)Proxy.getProxy(TestInterface.class).newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "哈哈";
            }
        });


        String str = testInterface.testStr();
        System.out.println(str);
    }
}
