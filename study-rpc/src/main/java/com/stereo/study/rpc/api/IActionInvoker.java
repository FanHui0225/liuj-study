package com.stereo.study.rpc.api;

/**
 * 
 * IServiceInvoker
 * 
 * @author 刘晶
 * @version 2013.12.19
 * 
 */
public interface IActionInvoker {

	public static final String BEAN_NAME = "serviceInvoker";

	Object getService(String serviceName);

	boolean invoke(IActionCall call);

	boolean invoke(IActionCall call, Object service);
}
