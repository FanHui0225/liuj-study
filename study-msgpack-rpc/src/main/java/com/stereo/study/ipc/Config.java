package com.stereo.study.ipc;

import com.stereo.study.ipc.server.skeleton.LiveExpired;
import com.stereo.study.ipc.util.NetUtils;
import java.net.InetSocketAddress;

public final class Config {
	private int soLinger = -1;
	private int timeout = 5 * 1000;
	private int connectTimeout = 5 * 1000;
	private int sendTimeout = 5 * 1000;
	private int readTimeout = 15 * 1000;
	private int idleTimeout = 30 * 1000;
	private int heartBeatQuantity = 5;
	private int heartBeatRate = 5 * 1000;// 5s
	private int heartBeatExpireInterval = 60 * 1000; // 60s
	private boolean ssl = false;
	private boolean tcpNoDelay = true;
	private boolean reuseAddress = true;
	private int sendBufferSize = 65535;
	private int receiveBufferSize = 65535;
	private boolean useEpoll = false;
	private int childNioEventThreads = 6; //cpu+1
	private int payload = 8 * 1024 * 1024;
	private InetSocketAddress remoteAddress = new InetSocketAddress(
			"0.0.0.0", 8099);
	private int businessPoolSize = 200;//业务处理线程
	private String businessPoolType = Constants.THREADPOOL_TYPE_CACHED;//线程池类型
	private String businessPoolQueueType = Constants.QUEUE_TYPE_NORMAL;  // 队列类型
	private int businessPoolQueueSize = 0; // 队列大小
	private LiveExpired liveExpired;//掉线监控
	private int connectAccepts;

	public Config(){
		this(8099);
	}

	public Config(int port){
		this("0.0.0.0", port);
	}

	public Config(String hostname, int port){
		if(NetUtils.isInvalidPort(port) || NetUtils.isInvalidHost(hostname))
		{
			throw new RuntimeException("hostname:" + hostname + " or port:"+port+" is Invalid");
		}
		remoteAddress = new InetSocketAddress(hostname,port);
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isSsl() {
		return ssl;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getSoLinger() {
		return soLinger;
	}

	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String addr, int port) {
		this.remoteAddress = new InetSocketAddress(addr, port);
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getHeartBeatRate() {
		return heartBeatRate;
	}

	public void setHeartBeatRate(int heartBeatRate) {
		this.heartBeatRate = heartBeatRate;
	}

	public int getHeartBeatExpireInterval() {
		return heartBeatExpireInterval;
	}

	public void setHeartBeatExpireInterval(int heartBeatExpireInterval) {
		this.heartBeatExpireInterval = heartBeatExpireInterval;
	}

	public String getHost(){
		return remoteAddress!=null ? remoteAddress.getHostName() : null;
	}

	public int getPort(){
		return remoteAddress!=null ? remoteAddress.getPort() : -1;
	}

	public boolean isUseEpoll() {
		return useEpoll;
	}

	public void setUseEpoll(boolean useEpoll) {
		this.useEpoll = useEpoll;
	}

	public int getChildNioEventThreads() {
		return childNioEventThreads;
	}

	public void setChildNioEventThreads(int childNioEventThreads) {
		this.childNioEventThreads = childNioEventThreads;
	}

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public int getPayload() {
		return payload;
	}

	public void setPayload(int payload) {
		this.payload = payload;
	}

	public int getBusinessPoolSize() {
		return businessPoolSize;
	}

	public String getBusinessPoolType() {
		return businessPoolType;
	}

	public String getBusinessPoolQueueType() {
		return businessPoolQueueType;
	}

	public int getBusinessPoolQueueSize() {
		return businessPoolQueueSize;
	}

	public void setBusinessPoolSize(int businessPoolSize) {
		this.businessPoolSize = businessPoolSize;
	}

	public void setBusinessPoolType(String businessPoolType) {
		this.businessPoolType = businessPoolType;
	}

	public void setBusinessPoolQueueType(String businessPoolQueueType) {
		this.businessPoolQueueType = businessPoolQueueType;
	}

	public void setBusinessPoolQueueSize(int businessPoolQueueSize) {
		this.businessPoolQueueSize = businessPoolQueueSize;
	}

	public LiveExpired getLiveExpired() {
		return liveExpired;
	}

	public void setLiveExpired(LiveExpired liveExpired) {
		this.liveExpired = liveExpired;
	}

	public int getHeartBeatQuantity() {
		return heartBeatQuantity;
	}

	public void setHeartBeatQuantity(int heartBeatQuantity) {
		this.heartBeatQuantity = heartBeatQuantity;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getConnectAccepts() {
		return connectAccepts;
	}

	public void setConnectAccepts(int connectAccepts) {
		this.connectAccepts = connectAccepts;
	}

	@Override
	public String toString() {
		return "Config{" +
				"soLinger=" + soLinger +
				", timeout=" + timeout +
				", connectTimeout=" + connectTimeout +
				", sendTimeout=" + sendTimeout +
				", readTimeout=" + readTimeout +
				", idleTimeout=" + idleTimeout +
				", heartBeatQuantity=" + heartBeatQuantity +
				", heartBeatRate=" + heartBeatRate +
				", heartBeatExpireInterval=" + heartBeatExpireInterval +
				", ssl=" + ssl +
				", tcpNoDelay=" + tcpNoDelay +
				", reuseAddress=" + reuseAddress +
				", sendBufferSize=" + sendBufferSize +
				", receiveBufferSize=" + receiveBufferSize +
				", useEpoll=" + useEpoll +
				", childNioEventThreads=" + childNioEventThreads +
				", payload=" + payload +
				", remoteAddress=" + remoteAddress +
				", businessPoolSize=" + businessPoolSize +
				", businessPoolType='" + businessPoolType + '\'' +
				", businessPoolQueueType='" + businessPoolQueueType + '\'' +
				", businessPoolQueueSize=" + businessPoolQueueSize +
				", liveExpired=" + liveExpired +
				", connectAccepts=" + connectAccepts +
				'}';
	}
}