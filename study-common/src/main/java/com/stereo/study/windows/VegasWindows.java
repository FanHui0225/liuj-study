package com.stereo.study.windows;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by liujing on 2024/3/28.
 */
public class VegasWindows extends AbstractWindows {
    private static final Logger LOG = LoggerFactory.getLogger(VegasWindows.class);
    private static final Function<Integer, Integer> LOG10 = Log10Function.create(0);

    public static class Builder {
        private int probeMultiplier = 30;
        private int initialLimit = 20;
        private int maxConcurrency = 1000;
        private double smoothing = 1.0;
        private Function<Integer, Integer> alphaFunc = (limit) -> 3 * LOG10.apply(limit.intValue());
        private Function<Integer, Integer> betaFunc = (limit) -> 6 * LOG10.apply(limit.intValue());
        private Function<Integer, Integer> thresholdFunc = (limit) -> LOG10.apply(limit.intValue());
        private Function<Double, Double> increaseFunc = (limit) -> limit + LOG10.apply(limit.intValue());
        private Function<Double, Double> decreaseFunc = (limit) -> limit - LOG10.apply(limit.intValue());

        private Builder() {
        }

        public Builder probeMultiplier(int probeMultiplier) {
            this.probeMultiplier = probeMultiplier;
            return this;
        }

        public Builder alpha(int alpha) {
            this.alphaFunc = (ignore) -> alpha;
            return this;
        }

        public Builder threshold(Function<Integer, Integer> threshold) {
            this.thresholdFunc = threshold;
            return this;
        }

        public Builder alpha(Function<Integer, Integer> alpha) {
            this.alphaFunc = alpha;
            return this;
        }

        public Builder beta(int beta) {
            this.betaFunc = (ignore) -> beta;
            return this;
        }

        public Builder beta(Function<Integer, Integer> beta) {
            this.betaFunc = beta;
            return this;
        }

        public Builder increase(Function<Double, Double> increase) {
            this.increaseFunc = increase;
            return this;
        }

        public Builder decrease(Function<Double, Double> decrease) {
            this.decreaseFunc = decrease;
            return this;
        }

        public Builder smoothing(double smoothing) {
            this.smoothing = smoothing;
            return this;
        }

        public Builder initialLimit(int initialLimit) {
            this.initialLimit = initialLimit;
            return this;
        }

        @Deprecated
        public Builder tolerance(double tolerance) {
            return this;
        }

        public Builder maxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }

        @Deprecated
        public Builder backoffRatio(double ratio) {
            return this;
        }

        public VegasWindows build() {
            return new VegasWindows(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static VegasWindows newDefault() {
        return newBuilder().build();
    }

    private volatile double estimatedLimit;
    private volatile long rtt_noload = 0;
    private final int maxLimit;
    private final double smoothing;
    private final Function<Integer, Integer> alphaFunc;
    private final Function<Integer, Integer> betaFunc;
    private final Function<Integer, Integer> thresholdFunc;
    private final Function<Double, Double> increaseFunc;
    private final Function<Double, Double> decreaseFunc;
    private final int probeMultiplier;
    private int probeCount = 0;
    private double probeJitter;

    private VegasWindows(Builder builder) {
        super(builder.initialLimit);
        this.estimatedLimit = builder.initialLimit;
        this.maxLimit = builder.maxConcurrency;
        this.alphaFunc = builder.alphaFunc;
        this.betaFunc = builder.betaFunc;
        this.increaseFunc = builder.increaseFunc;
        this.decreaseFunc = builder.decreaseFunc;
        this.thresholdFunc = builder.thresholdFunc;
        this.smoothing = builder.smoothing;
        this.probeMultiplier = builder.probeMultiplier;
        resetProbeJitter();
    }


    protected static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    private void resetProbeJitter() {
        probeJitter = ThreadLocalRandom.current().nextDouble(0.5, 1);
    }

    private boolean shouldProbe() {
        return probeJitter * probeMultiplier * estimatedLimit <= probeCount;
    }

    @Override
    protected int _update(long startTime, long rtt, int inflight, boolean didDrop) {
        checkArgument(rtt > 0, "rtt must be >0 but got " + rtt);

        probeCount++;
        if (shouldProbe()) {
            resetProbeJitter();
            probeCount = 0;
            rtt_noload = rtt;
            return (int) estimatedLimit;
        }

        if (rtt_noload == 0 || rtt < rtt_noload) {
            rtt_noload = rtt;
            return (int) estimatedLimit;
        }
        return updateEstimatedLimit(rtt, inflight, didDrop);
    }

    private int updateEstimatedLimit(long rtt, int inflight, boolean didDrop) {
        final int queueSize = (int) Math.ceil(estimatedLimit * (1 - (double) rtt_noload / rtt));
        double newLimit;
        if (didDrop) {
            newLimit = decreaseFunc.apply(estimatedLimit);
        } else if (inflight * 2 < estimatedLimit) {
            return (int) estimatedLimit;
        } else {
            int alpha = alphaFunc.apply((int) estimatedLimit);
            int beta = betaFunc.apply((int) estimatedLimit);
            int threshold = this.thresholdFunc.apply((int) estimatedLimit);
            System.out.println("===================================================================  VegasWindows.updateEstimatedLimit  <alpha=>" + alpha + "  <beta>=" + beta + "  <threshold>=" + threshold + "  <queueSize>=" + queueSize);
            if (queueSize <= threshold) {
                newLimit = estimatedLimit + beta;
            } else if (queueSize < alpha) {
                newLimit = increaseFunc.apply(estimatedLimit);
            } else if (queueSize > beta) {
                newLimit = decreaseFunc.apply(estimatedLimit);
            } else {
                return (int) estimatedLimit;
            }
        }

        newLimit = Math.max(1, Math.min(maxLimit, newLimit));
        newLimit = (1 - smoothing) * estimatedLimit + smoothing * newLimit;
        estimatedLimit = newLimit;
        return (int) estimatedLimit;
    }

    public static void main(String[] args) throws InterruptedException {
        Supplier<Long> clock = System::nanoTime;
        int initLimit = 10;
        int maxLimit = 40;
        int alphaLimit = (maxLimit / 3) * 1;
        int betaLimit = (maxLimit / 3) * 2;
        System.out.println("alphaLimit=" + alphaLimit);
        System.out.println("betaLimit=" + betaLimit);
        final VegasWindows windows = VegasWindows.newBuilder()
                .alpha(alphaLimit)
                .beta(betaLimit)
                .smoothing(1.0)
                .initialLimit(initLimit)
                .maxConcurrency(maxLimit)
                .build();

        //测试启动5个线程执行vages限流算法
        for (int i = 0; i < 3; i++) {
            final String t_name = StringUtils.upperCase("thread_test_" + i);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (; ; ) {
                        int inflight = ThreadLocalRandom.current().nextInt(0, 8);
                        long startTime = clock.get();
                        long expenses = ThreadLocalRandom.current().nextBoolean() ? 1000l : 3000l;
                        try {
                            Thread.sleep(expenses);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        windows.onSample(startTime, clock.get() - startTime, inflight, false);
                        System.out.println("(" + t_name + ") >>>>>>  Vegas Windows, limit=" + windows.getLimit() + "  inflight=" + inflight + " expenses=" + expenses);
//            inflight = windows.getLimit();
//            Thread.sleep(1000L);
//            Thread.sleep(5000L);
//            windows.onSample(0, TimeUnit.MILLISECONDS.toNanos(10), 14, false);
//            System.out.println("第二次 VegasWindows =》 " + windows.getLimit());
//            Thread.sleep(1000L);
                    }
                }
            }, t_name) {
                {
                    this.setDaemon(true);
                }
            }.start();
        }
        for (int i = 0; i < 3600; i++) {
            Thread.sleep(1000L);
        }
    }
}