package com.stereo.study.ipc.client;

public interface Callback<T> {

	void call(T value);

	Class<?> getAcceptValueType();
}
