package com.stereo.via.ipc.server.skeleton;

import com.stereo.via.ipc.server.api.IFunction;
import com.stereo.via.ipc.server.api.INotification;
import com.stereo.via.ipc.server.api.IObserver;

public class Observer implements IObserver {

	private Object context;

	private IFunction notify;

	public Observer(IFunction notify, Object context) {
		setNotifyContext(context);
		setNotifyMethod(notify);
	}

	public boolean compareNotifyContext(Object object) {
		return context == object;
	}

	public void notifyObserver(INotification notification) {
		getNotifyMethod().onNotification(notification);
	}

	public void setNotifyContext(Object notifyContext) {
		context = notifyContext;
	}

	public void setNotifyMethod(IFunction notifyMethod) {
		notify = notifyMethod;
	}

	public IFunction getNotifyMethod() {
		return notify;
	}

	public Object getNotifyContext() {
		return context;
	}
}