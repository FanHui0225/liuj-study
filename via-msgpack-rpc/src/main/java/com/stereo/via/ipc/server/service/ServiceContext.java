package com.stereo.via.ipc.server.service;

import com.stereo.via.event.AsyncDispatcher;
import com.stereo.via.event.Dispatcher;
import com.stereo.via.event.EventHandler;
import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.server.api.*;
import com.stereo.via.ipc.server.event.enums.ServiceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stereo.via.service.AbstractService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制层上下文
 * 
 * @author stereo
 */
public class ServiceContext extends AbstractService implements IServiceContext,
		Iterable<IService> {
	private Config config;
	private Dispatcher dispatcher;
	private IServiceHandler serviceHandler;
	protected Map<String, IService> serviceMap;
	protected Map<String, List<IObserver>> observerMap;
	public static Logger logger = LoggerFactory.getLogger(ServiceContext.class);
	private static ThreadLocal<WeakReference<Object>> threadLocal = new ThreadLocal<WeakReference<Object>>();

	public ServiceContext(Config config) {
		super("ServiceContext");
		this.config = config;
	}

	@Override
	protected void serviceInit() throws Exception {
		serviceMap = new ConcurrentHashMap<String, IService>();
		observerMap = new ConcurrentHashMap<String, List<IObserver>>();

		//事件处理器
		dispatcher = new AsyncDispatcher();
		((com.stereo.via.service.Service)dispatcher).init();

		//业务处理器
		serviceHandler = new ServiceHandler(this,config);
		((com.stereo.via.service.Service)serviceHandler).init();

		//注册业务处理器
		dispatcher.register(ServiceEnum.class, (EventHandler) serviceHandler);
	}

	@Override
	protected void serviceStart() throws Exception {
		if (dispatcher!=null)
			((com.stereo.via.service.Service)dispatcher).start();
		if (serviceHandler!=null) {
			((com.stereo.via.service.Service)serviceHandler).start();
		}
	}

	@Override
	protected void serviceStop() throws Exception {
		if (dispatcher!=null)
			((com.stereo.via.service.Service)dispatcher).stop();
		if (serviceHandler!=null)
			((com.stereo.via.service.Service)serviceHandler).stop();
	}

	public static Object getObjectLocal() {
		WeakReference<Object> ref = threadLocal.get();
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}

	public static void setObjectLocal(Object object) {
		if (object != null) {
			threadLocal.set(new WeakReference<Object>(object));
		} else {
			threadLocal.remove();
		}
	}

	@Override
	public void executeService(INotification note) {
		IService actionInstance = this.serviceMap.get(note.getName());
		if (actionInstance != null) {
			actionInstance.handleNotification(note);
		}
	}

	@Override
	public void registerService(final IService service) {
		if (this.serviceMap.containsKey(service.getServiceName()))
			return;
		this.serviceMap.put(service.getServiceName(), service);

		registerObserver(service.getServiceName(), new Observer(new IFunction() {
			public void onNotification(INotification notification) {
				executeService(notification);
			}
		}, this));
		service.setServiceContext(this);
		service.onRegister();
	}

	@Override
	public IService retrieveService(String serviceName) {
		if (null != serviceMap.get(serviceName)) {
			return this.serviceMap.get(serviceName);
		}
		for (IService action : this)
			return action.resolveService(serviceName);
		return null;
	}

	@Override
	public IService removeService(String serviceName) {
		if (hasService(serviceName)) {
			IService action = serviceMap.get(serviceName);
			removeObserver(serviceName, this);
			serviceMap.remove(serviceName);
			action.onRemove();
			return action;
		}
		return null;
	}

	@Override
	public boolean hasService(String serviceName) {
		return serviceMap.containsKey(serviceName);
	}

	@Override
	public void registerObserver(String notificationName, IObserver observer) {
		if (this.observerMap.get(notificationName) == null)
			this.observerMap.put(notificationName, new ArrayList<IObserver>());

		List<IObserver> observers = this.observerMap.get(notificationName);
		observers.add(observer);
	}

	@Override
	public void removeObserver(String notificationName, Object notifyContext) {
		List<IObserver> observers = observerMap.get(notificationName);
		if (observers != null) {
			for (int i = 0; i < observers.size(); i++) {
				Observer observer = (Observer) observers.get(i);
				if (observer.compareNotifyContext(notifyContext) == true)
					observers.remove(observer);
			}
			if (observers.size() == 0)
				observerMap.remove(notificationName);
		}
	}

	@Override
	public void notifyObservers(INotification note) {
		List<IObserver> observers_ref = observerMap.get(note.getName());
		if (observers_ref != null) {
			Object[] observers = (Object[]) observers_ref.toArray();
			for (int i = 0; i < observers.length; i++) {
				IObserver observer = (IObserver) observers[i];
				observer.notifyObserver(note);
			}
		}
	}

	@Override
	public Iterator<IService> iterator() {
		return serviceMap.values().iterator();
	}

	@Override
	public IServiceHandler getServiceHandler() {
		return serviceHandler;
	}

	@Override
	public Dispatcher getDispatcher() {
		return dispatcher;
	}
}