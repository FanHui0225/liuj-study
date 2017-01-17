package com.stereo.via.ipc.server.skeleton;

import com.stereo.via.event.AsyncDispatcher;
import com.stereo.via.event.Dispatcher;
import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.server.api.*;
import com.stereo.via.ipc.server.event.enums.HeartbeatEnum;
import com.stereo.via.ipc.server.event.enums.ServiceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stereo.via.service.AbstractService;
import com.stereo.via.service.Service;
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
public class SkeletonContext extends AbstractService implements ISkeletonContext,
		Iterable<IService> {
	public static Logger logger = LoggerFactory.getLogger(SkeletonContext.class);

	private Config config;
	private Dispatcher dispatcher;
	private Liveliness liveliness;
	private IServiceHandler serviceHandler;
	protected Map<String, IService> serviceMap;
	protected Map<String, List<IObserver>> observerMap;
	private static ThreadLocal<WeakReference<Object>> threadLocal = new ThreadLocal<WeakReference<Object>>();

	public SkeletonContext(Config config)
	{
		super("SkeletonContext");
		this.config = config;
	}

	@Override
	protected void serviceInit() throws Exception
	{
		serviceMap = new ConcurrentHashMap<String, IService>();
		observerMap = new ConcurrentHashMap<String, List<IObserver>>();

		//事件处理器
		dispatcher = new AsyncDispatcher();
		((Service)dispatcher).init();

		//业务处理器
		serviceHandler = new ServiceHandler(this, config);
		((Service)serviceHandler).init();

		//心跳检测
		liveliness = new Liveliness(config.getHeartBeatExpireInterval(),config.getLiveExpired());
		((Service)liveliness).init();

		//注册业务处理器
		dispatcher.register(ServiceEnum.class, serviceHandler);
		dispatcher.register(HeartbeatEnum.class, liveliness);
	}

	@Override
	protected void serviceStart() throws Exception {
		if (dispatcher!=null)
			((Service)dispatcher).start();
		if (serviceHandler!=null)
			((Service)serviceHandler).start();
		if (liveliness!=null)
			((Service)liveliness).start();
	}

	@Override
	protected void serviceStop() throws Exception {
		if (dispatcher!=null)
			((Service)dispatcher).stop();
		if (serviceHandler!=null)
			((Service)serviceHandler).stop();
		if (liveliness!=null)
			((Service)liveliness).stop();

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

	@Override
	public Liveliness getLiveliness() {
		return liveliness;
	}
}