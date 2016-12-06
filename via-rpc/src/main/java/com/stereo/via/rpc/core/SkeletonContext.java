package com.stereo.via.rpc.core;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashMap;

/**
 * 服务器Context
 * 
 * @author liujing
 */
public class SkeletonContext {

	private static final ThreadLocal<SkeletonContext> _localContext = new ThreadLocal<SkeletonContext>();

	private int _count;
	private String _objectId;
	private ServletRequest _request;
	private ServletResponse _response;
	private String _serviceName;
	private HashMap _headers = new HashMap();

	private SkeletonContext() {
	}

	/**
	 * 上下文开始
	 * 
	 * @param serviceName
	 * @param objectId
	 * @throws Exception
	 */
	public static void begin(String serviceName,
			String objectId) throws Exception {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context == null) {
			context = new SkeletonContext();
			_localContext.set(context);
		}
		context._serviceName = serviceName;
		context._objectId = objectId;
		context._count++;
	}

	public static void begin(ServletRequest request, ServletResponse response,
                             String serviceName, String objectId) throws ServletException {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context == null) {
			context = new SkeletonContext();
			_localContext.set(context);
		}
		context._request = request;
		context._response = response;
		context._serviceName = serviceName;
		context._objectId = objectId;
		context._count++;
	}

	/**
	 * 上下文结束
	 */
	public static void end() {
		SkeletonContext context = (SkeletonContext) _localContext.get();
		if (context != null && --context._count == 0) {
			context._request = null;
			context._response = null;
			context._headers.clear();
			_localContext.set(null);
		}
	}

	public void addHeader(String header, Object value) {
		_headers.put(header, value);
	}

	public Object getHeader(String header) {
		return _headers.get(header);
	}

	public static SkeletonContext getContext() {
		return (SkeletonContext) _localContext.get();
	}

	public static Object getContextHeader(String header) {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context.getHeader(header);
		else
			return null;
	}

	public static ServletRequest getContextRequest() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._request;
		else
			return null;
	}

	public static ServletResponse getContextResponse() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._response;
		else
			return null;
	}

	public static String getContextServiceName() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._serviceName;
		else
			return null;
	}

	public static String getContextObjectId() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._objectId;
		else
			return null;
	}

	@Deprecated
	public static ServletRequest getRequest() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._request;
		else
			return null;
	}

	@Deprecated
	public static String getServiceName() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._serviceName;
		else
			return null;
	}

	@Deprecated
	public static String getObjectId() {
		SkeletonContext context = (SkeletonContext) _localContext.get();

		if (context != null)
			return context._objectId;
		else
			return null;
	}
}