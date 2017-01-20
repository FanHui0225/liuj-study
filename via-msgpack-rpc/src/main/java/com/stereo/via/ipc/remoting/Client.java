
package com.stereo.via.ipc.remoting;

import com.stereo.via.ipc.exc.IpcRuntimeException;

public interface Client extends Endpoint, Channel {

    void reconnect() throws IpcRuntimeException;

    void doOpen() throws IpcRuntimeException;

    void doClose() throws IpcRuntimeException;

    void doConnect() throws IpcRuntimeException;

    void doDisConnect() throws IpcRuntimeException;

    Channel getChannel();
}