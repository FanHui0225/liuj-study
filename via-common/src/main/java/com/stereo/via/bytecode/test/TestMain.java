package com.stereo.via.bytecode.test;

import com.stereo.via.bytecode.Proxy;
import com.stereo.via.bytecode.test.TestInterface;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuj-ai on 2018/3/26.
 */
public class TestMain {

    public static void main(String[] args) throws Exception {

        TestInterface testInterface = (TestInterface) Proxy.getProxy(TestInterface.class,TestInterface2.class).newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "哈哈";
            }
        });


        String str = testInterface.testStr();
        System.out.println(str);
    }
}
