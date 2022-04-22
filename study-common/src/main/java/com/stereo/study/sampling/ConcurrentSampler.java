package com.stereo.study.sampling;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuj-ai on 2022/4/22.
 */
public class ConcurrentSampler implements Sampling {

    private final AtomicLong totalReqNum = new AtomicLong(0);
    private final AtomicLong currReqNum = new AtomicLong(0);

    @Override
    public boolean trySampling() {
        if (getRate() <= 0) {
            N();
            return false;
        }
        if (getRate() >= 10000) {
            Y();
            return true;
        }
        String tag = getTag();
        if (Y.equals(tag)) {
            return true;
        } else if (N.equals(tag)) {
            return false;
        } else if (getCurrReqNum() == 0) {
            //第一条采集
            incrCurrReqNum();
            incrTotalReqNum();
            Y();
            return true;
        }
        //总请求量
        long total = incrTotalReqNum();
        //当前请求量
        long cur = getCurrReqNum() + 1;
        Double rate = cur * 1.0 / total * 10000;
        if (rate.intValue() > getRate()) {
            N();
            return false;
        }
        incrCurrReqNum();
        Y();
        return true;
    }

    public long incrTotalReqNum() {
        return totalReqNum.incrementAndGet();
    }

    public long getTotalReqNum() {
        return totalReqNum.get();
    }

    public long incrCurrReqNum() {
        return currReqNum.incrementAndGet();
    }

    public long getCurrReqNum() {
        return currReqNum.get();
    }

    public static void main(String[] args) {
        ConcurrentSampler sampler = new ConcurrentSampler();
        for (int i = 0; i < 2; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    boolean ok = sampler.trySampling();
                    System.out.println("trySampling(" + index + ") " + ok);
                }
            });
            thread.start();
        }
    }
}
