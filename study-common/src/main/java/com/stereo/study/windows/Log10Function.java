package com.stereo.study.windows;

import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by liujing on 2024/3/28.
 */
public final class Log10Function implements Function<Integer, Integer> {
    static final int[] lookup = new int[1000];

    static {
        IntStream.range(0, 1000).forEach(i -> lookup[i] = Math.max(1, (int) Math.log10(i)));
    }

    private static final Log10Function INSTANCE = new Log10Function();

    public static Function<Integer, Integer> create(int baseline) {
        return INSTANCE.andThen(t -> t + baseline);
    }

    @Override
    public Integer apply(Integer t) {
        return t < 1000 ? lookup[t] : (int) Math.log10(t);
    }
}

