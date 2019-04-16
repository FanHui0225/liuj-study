package com.stereo.via.rpc.api;

public interface IObserver {

	public void setNotifyMethod(IFunction notifyMethod);

	public void setNotifyContext(Object notifyContext);

	public void notifyObserver(INotification notification);

	public boolean compareNotifyContext(Object object);
}
