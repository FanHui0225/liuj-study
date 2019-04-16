package com.stereo.study.rpc.client;

import com.stereo.study.rpc.api.IActionCall;
import com.stereo.study.rpc.api.IConnectionFactory;
import com.stereo.study.rpc.api.IProxyFactory;
import com.stereo.study.rpc.api.IUpload;
import com.stereo.study.rpc.core.IHandler;
import com.stereo.study.rpc.exc.RpcRuntimeException;
import com.stereo.study.rpc.io.*;
import com.stereo.study.rpc.io.factory.SerializerFactory;
import com.stereo.study.rpc.transport.Packet;
import com.stereo.study.rpc.utils.UUID;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * 客户端代理工厂(HTTP)
 * 
 * @author liujing
 */
public class RpcProxyFactory implements IProxyFactory {

	protected static Logger log = Logger.getLogger(RpcProxyFactory.class
			.getName());

	private final ClassLoader _loader;

	private SerializerFactory _serializerFactory;

	private IConnectionFactory _connFactory;

	private RemoteResolver _resolver;

	private String _user;
	private String _password;
	private String _basicAuth;

	private boolean _isOverloadEnabled = false;
	private boolean _isChunkedPost = true;

	private long _readTimeout = -1;
	private long _connectTimeout = -1;

	private int chunklen;

	public RpcProxyFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public RpcProxyFactory(ClassLoader loader)
	{
		_loader = loader;
		_resolver = new ProxyResolver(this);
	}

	public void setConnectionFactory(IConnectionFactory factory) {
		_connFactory = factory;
	}

	public IConnectionFactory getConnectionFactory() {
		if (_connFactory == null) {
			_connFactory = createConnectionFactory();
			_connFactory.setRPCProxyFactory(this);
		}
		return _connFactory;
	}

	public boolean isOverloadEnabled() {
		return _isOverloadEnabled;
	}

	public void setOverloadEnabled(boolean isOverloadEnabled) {
		_isOverloadEnabled = isOverloadEnabled;
	}

	public void setChunklen(int chunklen) {
		this.chunklen = chunklen;
	}

	public int getChunklen() {
		return chunklen;
	}

	public void setChunkedPost(boolean isChunked) {
		_isChunkedPost = isChunked;
	}

	public boolean isChunkedPost() {
		return _isChunkedPost;
	}

	public long getReadTimeout() {
		return _readTimeout;
	}

	public void setReadTimeout(long timeout) {
		_readTimeout = timeout;
	}

	public long getConnectTimeout() {
		return _connectTimeout;
	}

	public void setConnectTimeout(long timeout) {
		_connectTimeout = timeout;
	}

	public RemoteResolver getRemoteResolver() {
		return _resolver;
	}

	public void setSerializerFactory(SerializerFactory factory) {
		_serializerFactory = factory;
	}

	public SerializerFactory getSerializerFactory() {
		if (_serializerFactory == null)
			_serializerFactory = new SerializerFactory(_loader);
		return _serializerFactory;
	}

	protected IConnectionFactory createConnectionFactory() {
		String className = System.getProperty(IConnectionFactory.class
				.getName());
		IConnectionFactory factory = null;
		try {
			if (className != null) {
				ClassLoader loader = Thread.currentThread()
						.getContextClassLoader();
				Class<?> cl = Class.forName(className, false, loader);
				factory = (IConnectionFactory) cl.newInstance();
				return factory;
			}
		} catch (Exception e) {
			throw new RpcRuntimeException(e);
		}
		return new ConnectionFactory();
	}

	/**
	 * 封包
	 * 
	 * @param serviceName
	 * @param method
	 * @param returnType
	 * @param params
	 * @return
	 */
	private Packet packet(String serviceName, String method,
                          Class<?> returnType, Object[] params) {
		Packet packet = new Packet();
		UUID uuid = new UUID();
		uuid.setS_id(serviceName + "-" + method);
		packet.setId(uuid.toString());
		packet.setState(IActionCall.STATUS_PENDING);
		packet.setMethod(method);
		packet.setInterfaceName(serviceName);
		packet.setParams(params);
		packet.setReturnType(returnType);
		return packet;
	}

	public Object createAction(final Class<?> api, String urlName)
			throws MalformedURLException {
		return createAction(api, urlName, _loader);
	}

	/**
	 * 二层代理
	 * 
	 * @param api
	 * @param urlName
	 * @param loader
	 * @return
	 * @throws MalformedURLException
	 */
	public Object createAction(final Class<?> api, String urlName,
			ClassLoader loader) throws MalformedURLException {
		final IHandler<Packet> handler = (IHandler<Packet>) create(
				IHandler.class, urlName, loader);
		InvocationHandler invocationHandler = new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Packet result = handler.handle(packet(api.getSimpleName(),
						method.getName(), method.getReturnType(), args));
				if (result.getException() != null)
					throw new RpcRuntimeException(result.getException());
				return result == null ? null : result.getResult();
			}
		};
		return Proxy.newProxyInstance(loader, new Class[] { api },
				invocationHandler);
	}

	public Object create(Class api, String urlName)
			throws MalformedURLException {
		return create(api, urlName, _loader);
	}

	public Object create(Class<?> api, String urlName, ClassLoader loader)
			throws MalformedURLException {
		URL url = new URL(urlName);
		return create(api, url, loader);
	}

	/**
	 * 一层代理
	 * 
	 * @param api
	 * @param url
	 * @param loader
	 * @return
	 */
	public Object create(Class<?> api, URL url, ClassLoader loader) {
		if (api == null)
			throw new NullPointerException(
					"api must not be null for RpcProxyFactory.create()");
		InvocationHandler handler = null;
		handler = new RemoteProxy(url, this, api);
		return Proxy.newProxyInstance(loader, new Class[] { api }, handler);
	}

	public IUpload createUpload(String urlName, String filePath, String remotePath, String user, String passwd)
			throws Exception {
		return createUpload(urlName, filePath, remotePath , null ,user , passwd);
	}

	public IUpload createUpload(String urlName, String filePath, String remotePath, String remoteFileName , String user, String passwd)
			throws Exception {
		URL url = new URL(urlName);
		return createUpload(url, filePath, remotePath , remoteFileName ,user , passwd);
	}

	public IUpload createUpload(URL url, String filePath, String remotePath, String remoteFileName , String user, String passwd)
			throws Exception {
		InvocationHandler handler = null;
		handler = new UploadProxy(url, filePath ,remotePath, remoteFileName ,this, user,passwd);
		setChunklen(IUpload.FILE_SEGMENTED_BUF);
		return (IUpload) Proxy.newProxyInstance(_loader,
				new Class[] { IUpload.class }, handler);
	}

	protected AbstractInput getRPCInput(InputStream is) {
		AbstractInput in;
		in = new Input(is);
		in.setRemoteResolver(getRemoteResolver());
		in.setSerializerFactory(getSerializerFactory());
		return in;
	}

	protected AbstractOutput getRPCOutput(OutputStream os) {
		AbstractOutput out;
		out = new Output(os);
		out.setSerializerFactory(getSerializerFactory());
		return out;
	}
}