package com.stereo.via.rpc.exc;

public class RpcRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6865074239242615953L;
	private Throwable rootCause;

	public RpcRuntimeException() {
	}

	public RpcRuntimeException(String message) {
		super(message);
	}

	public RpcRuntimeException(String message, Throwable rootCause) {
		super(message);

		this.rootCause = rootCause;
	}

	public RpcRuntimeException(Throwable rootCause) {
		super(String.valueOf(rootCause));
		this.rootCause = rootCause;
	}

	public Throwable getRootCause() {
		return this.rootCause;
	}

	public Throwable getCause() {
		return getRootCause();
	}
}
