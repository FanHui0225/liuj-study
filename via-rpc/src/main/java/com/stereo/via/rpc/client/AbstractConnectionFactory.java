package com.stereo.via.rpc.client;

import com.stereo.via.rpc.api.IConnection;
import com.stereo.via.rpc.api.IConnectionFactory;
import com.stereo.via.rpc.api.IProxyFactory;

import java.io.IOException;
import java.net.URL;


abstract public class AbstractConnectionFactory implements IConnectionFactory {

	private IProxyFactory _proxyfactory;

	public void setRPCProxyFactory(IProxyFactory factory) {
		_proxyfactory = factory;
	}

	public IProxyFactory getRPCProxyFactory() {
		return _proxyfactory;
	}

	abstract public IConnection open(URL url) throws IOException;
}
