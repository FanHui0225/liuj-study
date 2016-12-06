package com.stereo.via.rpc.core.context;

import com.stereo.via.rpc.api.IActionCall;
import com.stereo.via.rpc.api.IActionInvoker;
import com.stereo.via.rpc.exc.MethodNotFoundException;
import com.stereo.via.rpc.exc.NotAllowedException;
import com.stereo.via.rpc.utils.ConversionUtils;
import com.stereo.via.rpc.utils.InvokeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ServiceNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 业务核心调用器
 * 
 * @author liujing
 * @version 2013.12.21
 * @see - 业务函数自动匹配、支持函数重载、没有匹配到函数自动找重载String类型
 */
public final class ActionInvoker implements IActionInvoker {

	private static class MethodCache {
		private String methodName;
		private Class<?>[] classes;
		private Method method;

		public MethodCache(String methodName, Class<?>[] classes, Method method) {
			this.methodName = methodName;
			this.classes = classes;
			this.method = method;
		}

		public Method findMethod(String methodName, Object[] args)
		{
			if (this.methodName.equals(methodName)
					&& args.length == classes.length) {
				for (int i = 0; i < args.length; i++)
					if (!args[i].getClass().isAssignableFrom(classes[i]))
						return null;
				return method;
			}
			return null;
		}
	}

	private static final Logger log = LoggerFactory
			.getLogger(ActionInvoker.class);

	private ActionContext actionContext;
	private Set<MethodCache> methodCaches = new CopyOnWriteArraySet<MethodCache>();

	protected ActionInvoker(ActionContext actionContext) {
		this.actionContext = actionContext;
	}

	@Override
	public Object getService(String serviceName) {
		return actionContext.retrieveAction(serviceName);
	}

	/**
	 * 调用ServiceCall
	 * 
	 * @param call
	 * @return
	 */
	@Override
	public boolean invoke(IActionCall call) {
		return invoke(call, getService(call.getInterfaceName()));
	}

	/**
	 * 调用带业务的ServiceCall
	 * 
	 * @param call
	 * @param service
	 * @return
	 */
	@Override
	public boolean invoke(IActionCall call, Object service) {
		if (service == null) {
			String interfaceName = call.getInterfaceName();
			call.setException(new ServiceNotFoundException(interfaceName));
			call.setStatus(IActionCall.STATUS_SERVICE_NOT_FOUND);
			log.warn("service not found: {}", interfaceName);
			return false;
		} else {
			String methodName = call.getMethodName();
			if (methodName.charAt(0) == '@') {
				methodName = methodName.substring(1);
			}
			Object[] args = call.getArguments();
			Object[] argsWithCall;
			if (args != null) {
				argsWithCall = new Object[args.length + 1];
				argsWithCall[0] = call;
				for (int i = 0; i < args.length; i++) {
					argsWithCall[i + 1] = args[i];
				}
			} else {
				argsWithCall = new Object[] { call };
			}
			Object[] methodResult = null;
			if (methodCaches.size() > 0) {
				Method method = null;
				for (MethodCache cache : methodCaches) {
					if (cache.findMethod(methodName, argsWithCall) != null) {
						method = cache.findMethod(methodName, argsWithCall);
						break;
					}
				}
				if (method != null) {
					methodResult = new Object[] { method, argsWithCall };
				}
			}
			if (methodResult == null) {
				methodResult = InvokeUtils.findMethodWithExactParameters(
						service, methodName, argsWithCall);
				if (methodResult.length == 0 || methodResult[0] == null) {
					methodResult = InvokeUtils.findMethodWithExactParameters(
							service, methodName, args);
					if (methodResult.length == 0 || methodResult[0] == null) {
						methodResult = InvokeUtils
								.findMethodWithListParameters(service,
										methodName, argsWithCall);
						if (methodResult.length == 0 || methodResult[0] == null) {
							methodResult = InvokeUtils
									.findMethodWithListParameters(service,
											methodName, args);
							if (methodResult.length == 0
									|| methodResult[0] == null) {
								log.error(
										"没有找到匹配参数的Method",
										new Object[] {
												methodName,
												(args == null ? Collections.EMPTY_LIST
														: Arrays.asList(args)),
												service });
								call.setStatus(IActionCall.STATUS_METHOD_NOT_FOUND);
								if (args != null && args.length > 0) {
									call.setException(new MethodNotFoundException(
											methodName, args));
								} else {
									call.setException(new MethodNotFoundException(
											methodName));
								}
								return false;
							}
						}
					}
				}
			}

			Object result = null;
			Method method = (Method) methodResult[0];
			Object[] params = (Object[]) methodResult[1];
			try {
				log.debug("Invoking method: ", method.toString());
				// if (method.getReturnType() != call.getReturnType()) {
				// call.setStatus(IActionCall.STATUS_METHOD_NOT_FOUND);
				// call.setException(new MethodNotFoundException(methodName
				// + " not match "));
				// return false;
				// }
				if (method.getReturnType() == Void.class) {
					method.invoke(service, params);
					call.setStatus(IActionCall.STATUS_SUCCESS_VOID);
				} else {
					result = method.invoke(service, params);
					log.debug("result: {}", result);
					call.setStatus(result == null ? IActionCall.STATUS_SUCCESS_NULL
							: IActionCall.STATUS_SUCCESS_RESULT);
				}
				call.setResult(result);
				methodCaches.add(new MethodCache(methodName, ConversionUtils
						.convertParams(params), method));

			} catch (NotAllowedException e) {
				call.setException(e);
				call.setStatus(IActionCall.STATUS_ACCESS_DENIED);
				return false;
			} catch (IllegalAccessException accessEx) {
				call.setException(accessEx);
				call.setStatus(IActionCall.STATUS_ACCESS_DENIED);
				log.error("Error executing call:", call);
				log.error("Service invocation error", accessEx);
				return false;
			} catch (InvocationTargetException invocationEx) {
				call.setException(invocationEx);
				call.setStatus(IActionCall.STATUS_INVOCATION_EXCEPTION);
				return false;
			} catch (Exception ex) {
				call.setException(ex);
				call.setStatus(IActionCall.STATUS_GENERAL_EXCEPTION);
				log.error("Error executing call: ", call);
				log.error("Service invocation error", ex);
				return false;
			}
			return true;
		}
	}
}