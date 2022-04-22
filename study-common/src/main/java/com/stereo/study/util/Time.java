package com.stereo.study.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    private final long millisecond = 1650359207350l;//1650379646753 1650356455717l
    private final int nanoOfMillisecond = 0;

    public LocalDateTime toLocalDateTime() {
        int date = (int) (this.millisecond / 86400000L);
        int time = (int) (this.millisecond % 86400000L);
        if (time < 0) {
            --date;
            time = (int) ((long) time + 86400000L);
        }

        long nanoOfDay = (long) time * 1000000L + (long) this.nanoOfMillisecond;
        LocalDate localDate = LocalDate.ofEpochDay((long) date);
        LocalTime localTime = LocalTime.ofNanoOfDay(nanoOfDay);
        return LocalDateTime.of(localDate, localTime);
    }

    public static void main(String[] args) {
        Timestamp timestamp = Timestamp.valueOf(new Time().toLocalDateTime());
        System.out.println(timestamp);
    }
}
