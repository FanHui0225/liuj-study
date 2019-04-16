package com.stereo.via.rpc.exc;

public class NotAllowedException extends RpcRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7552833324276839926L;

	public NotAllowedException() {
		super();
	}

	public NotAllowedException(String message) {
		super(message);
	}

}
