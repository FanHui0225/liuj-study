
package com.stereo.via.ipc.remoting;

import com.stereo.via.ipc.exc.ViaRuntimeException;

public interface Client extends Endpoint, Channel {

    void reconnect() throws ViaRuntimeException;

    void doOpen() throws ViaRuntimeException;

    void doClose() throws ViaRuntimeException;

    Channel getChannel();
}