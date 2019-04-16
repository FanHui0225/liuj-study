package com.stereo.study.rpc.exc;

public class ConnectionException extends RpcRuntimeException {

	public ConnectionException() {
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(String message, Throwable rootCause) {
		super(message, rootCause);
	}

	public ConnectionException(Throwable rootCause) {
		super(rootCause);
	}
}
