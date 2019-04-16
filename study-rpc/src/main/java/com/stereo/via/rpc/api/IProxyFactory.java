package com.stereo.via.rpc.api;

public interface IProxyFactory {

	public Object create(Class<?> api, String url)
			throws java.net.MalformedURLException;

	public Object createAction(Class<?> api, String urlName)
			throws java.net.MalformedURLException;
}
