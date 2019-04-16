package com.stereo.via.ipc.server.skeleton;

import com.stereo.via.ipc.Heartbeat;

/**
 * Created by stereo on 17-1-17.
 */
public interface LiveExpired
{
    void expire(Heartbeat heartbeat);
}
