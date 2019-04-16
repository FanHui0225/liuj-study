package com.stereo.via.rpc.core.context;

import com.stereo.via.rpc.api.INotification;

/**
 * 通知者
 * 
 * @author liujing
 */
public class Notifier {

	protected ActionContext actionContext = ActionContext.getInstance();

	public INotification sendNotification(String notificationName, Object body,
                                          String type) {
		INotification notification = new Notification(notificationName, body,
				type);
		actionContext.notifyObservers(notification);
		return notification;
	}

	public INotification sendNotification(String notificationName, Object body) {
		return sendNotification(notificationName, body,null);
	}

	public INotification sendNotification(String notificationName) {
		return sendNotification(notificationName,null,null);
	}
}
