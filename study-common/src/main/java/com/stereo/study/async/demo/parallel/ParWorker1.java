package com.stereo.study.async.demo.parallel;


import com.stereo.study.async.callback.ICallback;
import com.stereo.study.async.callback.IWorker;
import com.stereo.study.async.executor.timer.SystemClock;
import com.stereo.study.async.worker.WorkResult;
import com.stereo.study.async.wrapper.WorkerWrapper;

import java.util.Map;

/**
 * @author liuj-ai on 2019-11-20.
 */
public class ParWorker1 implements IWorker<String, String>, ICallback<String, String> {
    private long sleepTime = 1000;

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String action(String object, Map<String, WorkerWrapper> allWrappers) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result = " + SystemClock.now() + "---param = " + object + " from 1";
    }

    @Override
    public String defaultValue() {
        return "worker1--default";
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, String param, WorkResult<String> workResult) {
        if (success) {
            System.out.println("callback worker1 success--" + SystemClock.now() + "----" + workResult.getResult()
                    + "-threadName:" + Thread.currentThread().getName());
        } else {
            System.err.println("callback worker1 failure--" + SystemClock.now() + "----"  + workResult.getResult()
                    + "-threadName:" + Thread.currentThread().getName());
        }
    }

}
