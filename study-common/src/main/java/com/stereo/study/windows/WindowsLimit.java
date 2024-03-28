package com.stereo.study.windows;

import java.util.function.Consumer;

/**
 * Created by liujing on 2024/3/28.
 */
public interface WindowsLimit {

    int getLimit();

    void notifyOnChange(Consumer<Integer> consumer);

    void onSample(long startTime, long rtt, int inflight, boolean didDrop);
}
