package com.stereo.via.ipc.client;

public interface Callback<T> {

	void call(T value);

	Class<?> getAcceptValueType();
}
