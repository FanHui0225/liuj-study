
package com.stereo.study.ipc.remoting;

import com.stereo.study.ipc.Config;
import com.stereo.study.ipc.exc.ViaRuntimeException;

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
     * @throws ViaRuntimeException
     */
    void send(Object message) throws ViaRuntimeException;

    /**
     * send message.
     * 
     * @param message
     * @param sent 是否已发送完成
     */
    void send(Object message, boolean sent) throws ViaRuntimeException;

    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();
}