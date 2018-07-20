package com.stereo.via.util;

import java.text.SimpleDateFormat;

public final class Time {

    private static final long NANOSECONDS_PER_MILLISECOND = 1000000;

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT =
        new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSSZ");
            }
        };

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long monotonicNow() {
        return System.nanoTime() / NANOSECONDS_PER_MILLISECOND;
    }

    public static long monotonicNowNanos() {
        return System.nanoTime();
    }

    public static String formatTime(long millis) {
        return DATE_FORMAT.get().format(millis);
    }
}
