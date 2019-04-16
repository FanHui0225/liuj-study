package com.stereo.study.rpc.api;

import java.io.IOException;
import java.net.URL;

/**
 * 连接工厂接口
 * 
 * @author stereo
 */
public interface IConnectionFactory {

	public void setRPCProxyFactory(IProxyFactory factory);

	public IConnection open(URL url) throws IOException;
}
