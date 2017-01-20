
package com.stereo.via.ipc.remoting;

import com.stereo.via.ipc.exc.IpcRuntimeException;

public interface Client extends Endpoint, Channel {

    void reconnect() throws IpcRuntimeException;

    void doOpen() throws IpcRuntimeException;

    void doClose() throws IpcRuntimeException;

    Channel getChannel();
}