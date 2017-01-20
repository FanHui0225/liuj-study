
package com.stereo.via.ipc.remoting;

import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.exc.IpcRuntimeException;
import java.net.InetSocketAddress;

public interface Endpoint {

    /**
     * get config.
     * 
     * @return Config
     */
    Config getConfig();

    /**
     * get channel handler.
     * 
     * @return channel handler
     */
    ChannelHandler getChannelHandler();

    /**
     * get local address.
     * 
     * @return local address.
     */
    InetSocketAddress getLocalAddress();
    
    /**
     * send message.
     *
     * @param message
     * @throws IpcRuntimeException
     */
    void send(Object message) throws IpcRuntimeException;

    /**
     * send message.
     * 
     * @param message
     * @param sent 是否已发送完成
     */
    void send(Object message, boolean sent) throws IpcRuntimeException;

    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();
}