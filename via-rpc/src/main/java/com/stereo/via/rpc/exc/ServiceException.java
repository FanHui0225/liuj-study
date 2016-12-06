package com.stereo.via.rpc.exc;

public class ServiceException extends Exception {
	private String code;
	private Object detail;

	public ServiceException() {
	}

	public ServiceException(String message, String code, Object detail) {
		super(message);
		this.code = code;
		this.detail = detail;
	}

	public String getCode() {
		return code;
	}

	public Object getDetail() {
		return detail;
	}
}
