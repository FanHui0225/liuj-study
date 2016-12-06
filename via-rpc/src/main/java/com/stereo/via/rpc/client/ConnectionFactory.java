package com.stereo.via.rpc.client;

import com.stereo.via.rpc.api.IConnection;
import com.stereo.via.rpc.api.IConnectionFactory;
import com.stereo.via.rpc.api.IProxyFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 连接工厂
 * 
 * @author liujing
 */
public class ConnectionFactory extends AbstractConnectionFactory implements
		IConnectionFactory {

	private static final Logger log = Logger.getLogger(ConnectionFactory.class
			.getName());

	private RpcProxyFactory _proxyFactory;

	public void setRPCProxyFactory(IProxyFactory factory) {
		this._proxyFactory = (RpcProxyFactory)factory;
	}

	public IConnection open(URL url) throws IOException {
		if (log.isLoggable(Level.FINER))
			log.finer(this + " open(" + url + ")");

		URLConnection conn = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) conn;
		httpConn.setRequestProperty("Proxy-Connection", "Keep-Alive");
		httpConn.setRequestMethod("POST");
		conn.setDoInput(true);

		int chunklen = _proxyFactory.getChunklen();
		if (chunklen > 0)
			httpConn.setChunkedStreamingMode(chunklen);

		long connectTimeout = _proxyFactory.getConnectTimeout();

		if (connectTimeout >= 0)
			conn.setConnectTimeout((int) connectTimeout);

		conn.setDoOutput(true);

		long readTimeout = _proxyFactory.getReadTimeout();

		if (readTimeout > 0) {
			try {
				conn.setReadTimeout((int) readTimeout);
			} catch (Throwable e) {
			}
		}
		return new Connection(url, conn);
	}
}