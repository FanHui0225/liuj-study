package com.stereo.study.ipc.client;

import com.stereo.study.ipc.Constants;
import com.stereo.study.ipc.Packet;
import com.stereo.study.ipc.exc.*;
import com.stereo.study.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by stereo on 16-8-8.
 */
public class RemoteProxy implements InvocationHandler {

    private static Logger LOG = LoggerFactory.getLogger(RemoteProxy.class);

    private ClientProxy clientProxy;
    private Class<?> _type;
    private WeakHashMap<Method, String> _mangleMap = new WeakHashMap<Method, String>();

    public RemoteProxy(ClientProxy proxy, Class<?> type) {
        this.clientProxy = proxy;
        this._type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (clientProxy.getServiceState().equals(Service.STATE.STARTED)) {
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
                        return new Boolean(clientProxy.equals(handler.getClientProxy()));
                    } else if (methodName.equals("hashCode") && params.length == 0)
                        return new Integer(clientProxy.hashCode());
                    else if (methodName.equals("getType"))
                        return proxy.getClass().getInterfaces()[0].getName();
                    else if (methodName.equals("toString") && params.length == 0)
                        return "Proxy[" + clientProxy.toString() + "]";
                    mangleName = method.getName();
                    synchronized (_mangleMap) {
                        _mangleMap.put(method, mangleName);
                    }
                }
                //build packet
                final Packet packet = Packet.packetRequest(_type.getName(), method.getName(), method.getReturnType(), args);
                //LOG.debug("RemoteProxy invoke packet is " + packet);
                //发送请求
                AsyncFuture<Packet> future = clientProxy.sendPacket(packet);
                try {
                    Object resultPacket = future.get(getClientProxy().getConfig().getReadTimeout(), TimeUnit.MILLISECONDS);
                    //响应结果
                    return receiveResponse((Packet) resultPacket);
                } catch (InterruptedException ex) {
                    throw new ViaRuntimeException("ClientProxy >>> read packet timeout " + "packet : " + packet);
                }
            } else
                throw new ViaRuntimeException("ClientProxy >>> state is not started");
        } catch (Exception ex) {
            LOG.error("ClientProxy exc", ex);
            throw ex;
            //throw new ViaRuntimeException(ex);
        }
    }

    private Object receiveResponse(Packet response) {
        Object result = response.getResult();
        byte state = response.getState();
        String exc = null;
        switch (response.getState()) {
            case Constants.STATUS_PENDING:
                exc = "ClientProxy >>> request is not processed";
                break;
            case Constants.STATUS_SUCCESS_RESULT:
                if (isReturnType(response.getReturnType(), result.getClass())) {
                    return result;
                } else
                    exc = "ClientProxy >>> result type error";
                break;
            case Constants.STATUS_SUCCESS_NULL:
                return null;
            case Constants.STATUS_SUCCESS_VOID:
                return null;
            case Constants.STATUS_SERVICE_NOT_FOUND:
                exc = "ClientProxy >>> request Service is not found";
                throw new ServiceNotFoundException(exc);
            case Constants.STATUS_METHOD_NOT_FOUND:
                exc = "ClientProxy >>> request action method is not found";
                throw new MethodNotFoundException(exc);
            case Constants.STATUS_ACCESS_DENIED:
                exc = "ClientProxy >>> request action access denied";
                throw new NotAllowedException(exc);
            case Constants.STATUS_INVOCATION_EXCEPTION:
                exc = "ClientProxy >>> request action method invocation failed";
                throw new InvocationException(exc);
            case Constants.STATUS_GENERAL_EXCEPTION:
                exc = response.getException();
                break;
        }
        if (exc != null)
            throw new ViaRuntimeException(exc);
        return null;
    }

    private boolean isReturnType(Class<?> original, Class<?> current) {
        Map<Class, String> primitiveClassMap = Constants.primitiveClassMap;
        if (primitiveClassMap.get(original) != null)
            return primitiveClassMap.get(original).equals(primitiveClassMap.get(current));
        else
            return original.isAssignableFrom(current);
    }

    private ClientProxy getClientProxy() {
        return clientProxy;
    }
}
