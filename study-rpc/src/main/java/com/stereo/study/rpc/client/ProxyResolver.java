package com.stereo.study.rpc.client;

import com.stereo.study.rpc.io.RemoteResolver;

import java.io.IOException;

public class ProxyResolver implements RemoteResolver {

	private RpcProxyFactory _factory;

	public ProxyResolver(RpcProxyFactory factory) {
		_factory = factory;
	}

	public Object lookup(String type, String url) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Class<?> api = Class.forName(type, false, loader);
			return _factory.create(api, url);
		} catch (Exception e) {
			throw new IOException(String.valueOf(e));
		}
	}
}