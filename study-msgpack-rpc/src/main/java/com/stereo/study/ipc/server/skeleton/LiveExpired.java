package com.stereo.study.ipc.server.skeleton;

import com.stereo.study.ipc.Heartbeat;

/**
 * Created by stereo on 17-1-17.
 */
public interface LiveExpired
{
    void expire(Heartbeat heartbeat);
}
