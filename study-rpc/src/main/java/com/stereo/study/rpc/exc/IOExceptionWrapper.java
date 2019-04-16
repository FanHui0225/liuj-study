package com.stereo.study.rpc.exc;

import java.io.IOException;

public class IOExceptionWrapper extends IOException {

	private Throwable _cause;

	public IOExceptionWrapper(Throwable cause) {
		super(cause.toString());
		_cause = cause;
	}

	public IOExceptionWrapper(String msg, Throwable cause) {
		super(msg);
		_cause = cause;
	}

	public Throwable getCause() {
		return _cause;
	}
}
