package com.stereo.via.ipc.util;

public class MonotonicClock implements Clock {

  public long getTime() {
    return Time.monotonicNow();
  }
}
