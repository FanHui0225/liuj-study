package com.stereo.via.dubbo.cebbank.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by stereo on 17-3-31.
 */
public class CebBankServer {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring-dubbo-provider.xml"});
        context.start();
        System.out.println("CebBankServer start...");
        LockSupport.park(Thread.currentThread());
    }
}
