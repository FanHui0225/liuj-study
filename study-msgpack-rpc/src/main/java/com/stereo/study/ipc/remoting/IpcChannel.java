package com.stereo.study.ipc.remoting;

import com.stereo.study.ipc.Config;
import com.stereo.study.ipc.exc.ViaRuntimeException;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IpcChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(IpcChannel.class);

    private static final ConcurrentMap<io.netty.channel.Channel , IpcChannel> channelMap = new ConcurrentHashMap<io.netty.channel.Channel , IpcChannel>();

    private final io.netty.channel.Channel  channel;

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private IpcChannel(io.netty.channel.Channel channel, Config config, ChannelHandler handler){
        super(config, handler);
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
    }

    public static IpcChannel getChannel(io.netty.channel.Channel  ch) {
        if (ch == null) {
            return null;
        }
        IpcChannel ret = channelMap.get(ch);
        if (ret == null) {
           return null;
        }
        return ret;
    }

    public static IpcChannel getOrAddChannel(io.netty.channel.Channel  ch, Config config, ChannelHandler handler) {
        if (ch == null) {
            return null;
        }
        IpcChannel ret = channelMap.get(ch);
        if (ret == null) {
            IpcChannel nc = new IpcChannel(ch, config, handler);
            if (ch.isActive()) {
                ret = channelMap.putIfAbsent(ch, nc);
            }
            if (ret == null) {
                ret = nc;
            }
        }
        return ret;
    }

    public static void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && ! ch.isActive()) {
            channelMap.remove(ch);
        }
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public void send(Object message, boolean sent) throws ViaRuntimeException {
        super.send(message, sent);
        
        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message).sync();
            if (sent) {
                timeout = getConfig().getSendTimeout();
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new ViaRuntimeException("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        
        if(! success) {
            throw new ViaRuntimeException("Failed to send message " + message + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }
    }

    public void closeChannel() {
        try {
            super.closeChannel();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            attributes.clear();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close netty channel " + channel);
            }
            channel.close().syncUninterruptibly();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        if (value == null) { // The null value unallowed in the ConcurrentHashMap.
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IpcChannel other = (IpcChannel) obj;
        if (channel == null) {
            if (other.channel != null) return false;
        } else if (!channel.equals(other.channel)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "IpcChannel [channel=" + channel + "]";
    }

}