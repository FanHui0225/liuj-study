package com.stereo.via.rpc.api;

import java.util.Set;

/**
 * @author stereo
 * @version 2013.12.19
 */
public interface IActionCall {

	/**
	 * ״̬
	 */
	public static final byte STATUS_PENDING = 0x01; // 未处理
	public static final byte STATUS_SUCCESS_RESULT = 0x02; // 调用成功并返回结果
	public static final byte STATUS_SUCCESS_NULL = 0x03; // 调用成功并返回NULL
	public static final byte STATUS_SUCCESS_VOID = 0x04; // 调用成功无结果
	public static final byte STATUS_SERVICE_NOT_FOUND = 0x10;// 业务未找到
	public static final byte STATUS_METHOD_NOT_FOUND = 0x11; // 方法未找到
	public static final byte STATUS_ACCESS_DENIED = 0x12; // 拒绝访问
	public static final byte STATUS_INVOCATION_EXCEPTION = 0x13;// 调用时异常
	public static final byte STATUS_GENERAL_EXCEPTION = 0x14; // 一般异常
	public static final byte STATUS_APP_SHUTTING_DOWN = 0x15; // 应用程序关闭
	public static final byte STATUS_NOT_CONNECTED = 0x20; // 未连接

	public abstract String getId();

	public abstract boolean isSuccess();

	public abstract Class<?> getReturnType();

	public abstract Object getResult();

	public abstract Object getResultPacket();

	public abstract void setResult(Object result);

	public abstract String getMethodName();

	public abstract String getInterfaceName();

	public abstract Object[] getArguments();

	public abstract byte getStatus();

	public abstract Exception getException();

	public abstract void setStatus(byte status);

	public abstract Set<ICallback> getCallbacks();

	public abstract void registerCallback(ICallback callback);

	public abstract void setException(Exception exception);
}