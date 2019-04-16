package com.stereo.study.ipc.util;

public class MonotonicClock implements Clock {

  public long getTime() {
    return Time.monotonicNow();
  }
}
