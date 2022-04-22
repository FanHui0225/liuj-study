package com.stereo.study.sampling;


/**
 * Created by liuj-ai on 2022/4/22.
 */
public interface Sampling {

    String N = "N", Y = "Y";

    boolean trySampling();

    default void Y() {
        System.out.println("Sampling.Y");
    }

    default void N() {
        System.out.println("Sampling.N");
    }

    default String getTag() {
        return "";
    }

    default int getRate() {
        return 5000;
    }
}
