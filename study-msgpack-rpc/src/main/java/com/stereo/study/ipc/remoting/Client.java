
package com.stereo.study.ipc.remoting;

import com.stereo.study.ipc.exc.ViaRuntimeException;

public interface Client extends Endpoint, Channel {

    void reconnect() throws ViaRuntimeException;

    void doOpen() throws ViaRuntimeException;

    void doClose() throws ViaRuntimeException;

    Channel getChannel();
}