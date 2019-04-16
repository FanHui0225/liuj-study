package com.stereo.via.wheeltimerqueue.test;

import com.stereo.via.wheeltimerqueue.AbstractTask;
import com.stereo.via.wheeltimerqueue.QueueBootstrap;
import com.stereo.via.wheeltimerqueue.WheelQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WheelTimerQueueTest {
    private static final Logger LOG = LoggerFactory.getLogger(WheelTimerQueueTest.class);


    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test1() {
        QueueBootstrap queueBootstrap = new QueueBootstrap();
        WheelQueue wheelQueue = queueBootstrap.start();

        wheelQueue.add(new AbstractTask("9527") {

            @Override
            public void run() {
                LOG.info("add task. id=" + this.getId());
            }

        }, 5);

        wheelQueue.add(new AbstractTask("9528") {

            @Override
            public void run() {
                LOG.info("running task. id=" + this.getId());
            }

        }, 8);

        wheelQueue.add(new AbstractTask("9529") {

            @Override
            public void run() {
                LOG.info("running task. id=" + this.getId());
            }

        }, 9);

        while (true) {

        }
    }

    public static void test2() {
        int threadCount = 3;
        final int sleep = 200;
        final int secondsRandom = 8000;
        QueueBootstrap queueBootstrap = new QueueBootstrap();
        final WheelQueue wheelQueue = queueBootstrap.start();
        Thread thread;

        for (int i = 1; i <= threadCount; i++) {
            thread = new Thread(new Runnable() {
                final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

                @Override
                public void run() {
                    while (true) {
                        wheelQueue.add(new AbstractTask(generateId()) {

                            @Override
                            public void run() {
                                LOG.debug("business processes. id=" + this.getId());
                            }

                        }, threadLocalRandom.nextInt(0, secondsRandom));

                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

            thread.start();

        }

        while (true) {
        }
    }

    static AtomicInteger counter = new AtomicInteger(0);

    private static String generateId() {
        String s = String.format("C%05d", counter.incrementAndGet());

        return s;
    }

}
