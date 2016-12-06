package com.stereo.via.rpc.client;

import com.stereo.via.rpc.api.IConnection;
import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.io.Remote;
import com.stereo.via.rpc.core.AbstractSkeleton;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import com.stereo.via.rpc.io.AbstractOutput;
import com.stereo.via.rpc.transport.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * (Http)远程调用代理
 * 
 * @author liujing
 */
public class RemoteProxy implements InvocationHandler, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2926143283603731685L;

	private static final Logger log = Logger.getLogger(RemoteProxy.class
			.getName());

	protected RpcProxyFactory _factory;

	private WeakHashMap<Method, String> _mangleMap = new WeakHashMap<Method, String>();

	private Class<?> _type;
	private URL _url;

	protected RemoteProxy(URL url, RpcProxyFactory factory) {
		this(url, factory, null);
	}

	protected RemoteProxy(URL url, RpcProxyFactory factory, Class<?> type) {
		_factory = factory;
		_url = url;
		_type = type;
	}

	public URL getURL() {
		return _url;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String mangleName;

		synchronized (_mangleMap) {
			mangleName = _mangleMap.get(method);
		}

		if (mangleName == null) {
			String methodName = method.getName();
			Class<?>[] params = method.getParameterTypes();
			if (methodName.equals("equals") && params.length == 1
					&& params[0].equals(Object.class)) {
				Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass()))
					return Boolean.FALSE;
				Object proxyHandler = Proxy.getInvocationHandler(value);
				if (!(proxyHandler instanceof RemoteProxy))
					return Boolean.FALSE;
				RemoteProxy handler = (RemoteProxy) proxyHandler;
				return new Boolean(_url.equals(handler.getURL()));
			} else if (methodName.equals("hashCode") && params.length == 0)
				return new Integer(_url.hashCode());
			else if (methodName.equals("getType"))
				return proxy.getClass().getInterfaces()[0].getName();
			else if (methodName.equals("getURL"))
				return _url.toString();
			else if (methodName.equals("toString") && params.length == 0)
				return "Proxy[" + _url + "]";
			if (!_factory.isOverloadEnabled())
				mangleName = method.getName();
			else
				mangleName = mangleName(method);

			synchronized (_mangleMap) {
				_mangleMap.put(method, mangleName);
			}
		}
		InputStream is = null;
		IConnection conn = null;
		try {
			if (args[0] instanceof Packet) {
				final Packet packet = (Packet) args[0];
				conn = sendRequest(mangleName, args);
				is = getInputStream(conn);
				AbstractInput in = _factory.getRPCInput(is);
				Object result = in.readReply(method.getReturnType());
				if (result == null) {
					throw new RpcRuntimeException(packet.getInterfaceName()
							+ "." + packet.getMethod() + " execute failed !");
				} else
					return result;
			} else
				throw new RpcRuntimeException(" packet error !");
		} catch (Exception e) {
			throw new RpcRuntimeException(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				log.log(Level.FINE, e.toString(), e);
			}

			try {
				if (conn != null)
					conn.destroy();
			} catch (Exception e) {
				log.log(Level.FINE, e.toString(), e);
			}
		}
	}

	protected InputStream getInputStream(IConnection conn) throws IOException {
		InputStream is = conn.getInputStream();
		if ("deflate".equals(conn.getContentEncoding())) {
			is = new InflaterInputStream(is, new Inflater(true));
		}
		return is;
	}

	protected String mangleName(Method method) {
		Class<?>[] param = method.getParameterTypes();
		if (param == null || param.length == 0)
			return method.getName();
		else
			return AbstractSkeleton.mangleName(method, false);
	}

	protected IConnection sendRequest(String methodName, Object[] args)
			throws IOException {
		IConnection conn = null;
		conn = _factory.getConnectionFactory().open(_url);
		boolean isValid = false;

		try {
			addRequestHeaders(conn);
			OutputStream os = null;
			try {
				os = conn.getOutputStream();
			} catch (Exception e) {
				throw new RpcRuntimeException(e);
			}
			AbstractOutput out = _factory.getRPCOutput(os);
			out.call(methodName, args);
			out.flush();
			conn.sendRequest();
			isValid = true;
			return conn;
		} finally {
			if (!isValid && conn != null)
				conn.destroy();
		}
	}

	protected void addRequestHeaders(IConnection conn) {
		conn.addHeader("Content-Type", "x-application/rpc");
		conn.addHeader("Accept-Encoding", "deflate");
	}

	protected void parseResponseHeaders(URLConnection conn) {
	}

	public Object writeReplace() {
		return new Remote(_type.getName(), _url.toString());
	}
}