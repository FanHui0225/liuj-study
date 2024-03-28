package com.stereo.study.windows;

/**
 * Created by liujing on 2024/3/28.
 */
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Created by liuj-ai on 2021/7/5.
 */
public abstract class AbstractWindows implements WindowsLimit {

    private volatile int limit;
    private final List<Consumer<Integer>> listeners = new CopyOnWriteArrayList<>();

    protected AbstractWindows(int initialLimit) {
        this.limit = initialLimit;
    }

    @Override
    public final synchronized void onSample(long startTime, long rtt, int inflight, boolean didDrop) {
        setLimit(_update(startTime, rtt, inflight, didDrop));
    }

    protected abstract int _update(long startTime, long rtt, int inflight, boolean didDrop);

    @Override
    public final int getLimit() {
        return limit;
    }

    protected synchronized void setLimit(int newLimit) {
        if (newLimit != limit) {
            limit = newLimit;
            listeners.forEach(listener -> listener.accept(newLimit));
        }
    }

    public void notifyOnChange(Consumer<Integer> consumer) {
        this.listeners.add(consumer);
    }
}
