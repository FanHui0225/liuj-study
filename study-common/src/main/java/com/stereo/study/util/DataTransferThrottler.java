package com.stereo.study.util;

import static com.stereo.study.util.Time.monotonicNow;

public class DataTransferThrottler {
    private final long period;          // period over which bw is imposed
    private final long periodExtension; // Max period over which bw accumulates.
    private long bytesPerPeriod;  // total number of bytes can be sent in each period
    private long curPeriodStart;  // current period starting time
    private long curReserve;      // remaining bytes can be sent in the period
    private long bytesAlreadyUsed;

    public DataTransferThrottler(long bandwidthPerSec) {
        this(500, bandwidthPerSec);  // by default throttling period is 500ms
    }

    public DataTransferThrottler(long period, long bandwidthPerSec) {
        this.curPeriodStart = monotonicNow();
        this.period = period;
        this.curReserve = this.bytesPerPeriod = bandwidthPerSec * period / 1000;
        this.periodExtension = period * 3;
    }

    public synchronized long getBandwidth() {
        return bytesPerPeriod * 1000 / period;
    }

    public synchronized void setBandwidth(long bytesPerSecond) {
        if (bytesPerSecond <= 0) {
            throw new IllegalArgumentException("" + bytesPerSecond);
        }
        bytesPerPeriod = bytesPerSecond * period / 1000;
    }

    public synchronized void throttle(long numOfBytes) {
        throttle(numOfBytes, null);
    }

    public synchronized void throttle(long numOfBytes, Canceler canceler) {
        if (numOfBytes <= 0) {
            return;
        }

        curReserve -= numOfBytes;
        bytesAlreadyUsed += numOfBytes;

        while (curReserve <= 0) {
            if (canceler != null && canceler.isCancelled()) {
                return;
            }
            long now = monotonicNow();
            long curPeriodEnd = curPeriodStart + period;

            if (now < curPeriodEnd) {
                // Wait for next period so that curReserve can be increased.
                try {
                    wait(curPeriodEnd - now);
                } catch (InterruptedException e) {
                    // Abort throttle and reset interrupted status to make sure other
                    // interrupt handling higher in the call stack executes.
                    Thread.currentThread().interrupt();
                    break;
                }
            } else if (now < (curPeriodStart + periodExtension)) {
                curPeriodStart = curPeriodEnd;
                curReserve += bytesPerPeriod;
            } else {
                // discard the prev period. Throttler might not have
                // been used for a long time.
                curPeriodStart = now;
                curReserve = bytesPerPeriod - bytesAlreadyUsed;
            }
        }

        bytesAlreadyUsed -= numOfBytes;
    }
}
