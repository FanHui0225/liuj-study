package com.stereo.study.rpc.exc;

import java.io.IOException;

public class ProtocolException extends IOException {
	private Throwable rootCause;

	public ProtocolException() {
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(String message, Throwable rootCause) {
		super(message);

		this.rootCause = rootCause;
	}

	public ProtocolException(Throwable rootCause) {
		super(String.valueOf(rootCause));

		this.rootCause = rootCause;
	}

	public Throwable getRootCause() {
		return rootCause;
	}

	public Throwable getCause() {
		return getRootCause();
	}
}
