package com.stereo.study.util;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuj-ai on 2020/9/11.
 */
public class TestLock {

    private AtomicLong a1 = new AtomicLong(-1);
    private AtomicLong a2 = new AtomicLong();
    private long c = 0;

    public void lock() throws Exception {
        long tId = Thread.currentThread().getId();
        if (tId != a1.get() && !a1.compareAndSet(-1, tId)) {
            throw new Exception();
        }
        a2.incrementAndGet();
    }


    public void unlock() {
        if (a2.decrementAndGet() == 0) {
            a1.set(-1l);
        }
    }

    private void foo() throws Exception {
        lock();
        c++;
        unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        final TestLock testLock = new TestLock();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    boolean f;
                    do {
                        try {
                            testLock.foo();
                            f = false;
                        } catch (Exception e) {
                            f = true;
                        }
                    } while (f);
                }
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(10L);
        }
        System.out.println(testLock.a1.get());
        System.out.println(testLock.a2.get());
        System.out.println(testLock.c);
    }
}
