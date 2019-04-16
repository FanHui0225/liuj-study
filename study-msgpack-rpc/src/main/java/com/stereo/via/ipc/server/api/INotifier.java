package com.stereo.via.ipc.server.api;

public interface INotifier {

	public INotification sendNotification(String notificationName, Object body,
                                          String type);

	public INotification sendNotification(String notificationName, Object body);

	public INotification sendNotification(String notificationName);
}
