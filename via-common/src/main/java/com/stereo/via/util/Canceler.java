package com.stereo.via.util;

public class Canceler {
    private volatile String cancelReason = null;

    public void cancel(String reason) {
        this.cancelReason = reason;
    }

    public boolean isCancelled() {
        return cancelReason != null;
    }

    public String getCancellationReason() {
        return cancelReason;
    }
}