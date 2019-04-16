package com.stereo.via.rpc.exc;

public class FieldException extends ProtocolException {
	public FieldException() {
	}

	public FieldException(String message) {
		super(message);
	}

	public FieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldException(Throwable cause) {
		super(cause);
	}
}
