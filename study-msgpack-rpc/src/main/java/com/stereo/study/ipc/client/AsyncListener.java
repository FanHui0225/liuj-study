package com.stereo.study.ipc.client;

public interface AsyncListener<T> {
	void asyncReturn(T returnValue);
}
