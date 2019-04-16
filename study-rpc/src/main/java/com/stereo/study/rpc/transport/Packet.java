package com.stereo.study.rpc.transport;

import com.stereo.study.rpc.core.context.AttributeStore;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 信息包
 * 
 * @author liujing
 * @version 2014.5.21
 */
public class Packet extends AttributeStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3447224470014044569L;

	private String id;

	private byte type;

	private byte state;

	private String interfaceName;

	private String method;

	private Object[] params;

	private Object result;

	private Class<?> returnType;

	private Exception exception;

	public Packet() {
	}

	public Packet(Object result) {
		this.result = result;
	}

	public Packet(String interfaceName, String method, Object[] params) {
		this.interfaceName = interfaceName;
		this.method = method;
		this.params = params;
	}

	public Packet(String id, byte type, byte state, String interfaceName,
                  String method, Object[] params) {
		super();
		this.id = id;
		this.type = type;
		this.state = state;
		this.interfaceName = interfaceName;
		this.method = method;
		this.params = params;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "Packet [id=" + id + ", type=" + type + ", state=" + state
				+ ", interfaceName=" + interfaceName + ", method=" + method
				+ ", params=" + Arrays.toString(params) + ", result=" + result
				+ ", returnType=" + returnType + ", exception=" + exception
				+ "]";
	}
}