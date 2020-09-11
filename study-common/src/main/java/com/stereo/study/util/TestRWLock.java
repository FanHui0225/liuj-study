package com.stereo.study.util;

/**
 * Created by liuj-ai on 2020/9/1.
 */
public class TestRWLock {

    private volatile int readCount = 0;
    private volatile int writeCount = 0;

    synchronized void readLock() throws InterruptedException {
        while (writeCount > 0) {
            wait();
        }
        readCount++;
    }

    synchronized void readUnLock() {
        readCount--;
        notifyAll();
    }

    synchronized void writeLock() throws InterruptedException {
        while (writeCount > 0) {
            wait();
        }
        writeCount++;
        while (readCount > 0) {
            wait();
        }
    }

    synchronized void writeUnLock() {
        readCount--;
        notifyAll();
    }

    public static void main(String[] args) {
        
    }
}
