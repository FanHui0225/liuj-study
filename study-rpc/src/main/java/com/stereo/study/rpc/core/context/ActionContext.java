package com.stereo.study.rpc.core.context;

import com.stereo.study.rpc.api.*;
import com.stereo.study.rpc.transport.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制层上下文
 * 
 * @author liujing
 */
public class ActionContext extends AttributeStore implements IActionContext,
		Iterable<IAction> {

	protected IActionInvoker actionInvoker;
	protected Map<String, IAction> actionMap;
	protected Map<String, List<IObserver>> observerMap;
	public static Logger logger = LoggerFactory.getLogger(ActionContext.class);
	private static ThreadLocal<WeakReference<Object>> threadLocal = new ThreadLocal<WeakReference<Object>>();

	private ActionContext() {
		actionInvoker = new ActionInvoker(this);
		actionMap = new ConcurrentHashMap<String, IAction>();
		observerMap = new ConcurrentHashMap<String, List<IObserver>>();
		initializeActionContext();
	}

	private static class ActionContextHolder
	{
		private static ActionContext instance = new ActionContext();
	}

	protected void initializeActionContext() {
	}

	public static ActionContext getInstance()
	{
		return ActionContextHolder.instance;
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
	public void executeAction(INotification note) {
		IAction actionInstance = this.actionMap.get(note.getName());
		if (actionInstance != null) {
			actionInstance.handleNotification(note);
		}
	}

	@Override
	public void registerAction(final IAction action) {
		if (this.actionMap.containsKey(action.getActionName()))
			return;
		this.actionMap.put(action.getActionName(), action);

		registerObserver(action.getActionName(), new Observer(new IFunction() {
			public void onNotification(INotification notification) {
				executeAction(notification);
			}
		}, this));
		action.onRegister();
	}

	@Override
	public IAction retrieveAction(String actionName) {
		if (null != actionMap.get(actionName)) {
			return this.actionMap.get(actionName);
		}
		for (IAction action : this)
			return action.resolveAction(actionName);
		return null;
	}

	@Override
	public IAction removeAction(String actionName) {
		if (hasAction(actionName)) {
			IAction action = actionMap.get(actionName);
			removeObserver(actionName, this);
			actionMap.remove(actionName);
			action.onRemove();
			return action;
		}
		return null;
	}

	@Override
	public boolean hasAction(String actionName) {
		return actionMap.containsKey(actionName);
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
	public Iterator<IAction> iterator() {
		return actionMap.values().iterator();
	}

	@Override
	public boolean invoke(Packet message) {
		return actionInvoker.invoke(new ActionCall(message));
	}
}