package com.stereo.via.ipc.client;

public interface AsyncListener<T> {
	void asyncReturn(T returnValue);
}
