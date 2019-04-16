package com.stereo.study.rpc.exc;

public class MethodSerializationException extends RpcRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6124079065009105984L;

	public MethodSerializationException() {
	}

	public MethodSerializationException(String message) {
		super(message);
	}

	public MethodSerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodSerializationException(Throwable cause) {
		super(cause);
	}
}
