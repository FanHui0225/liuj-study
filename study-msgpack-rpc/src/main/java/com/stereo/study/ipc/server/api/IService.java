package com.stereo.study.ipc.server.api;

/**
 * 控制接口
 * 
 * @author stereo
 */
public interface IService extends INotifier {

	public void onRemove();

	public void onRegister();

	public String getServiceName();

	public IService resolveService(String actionName);

	public void handleNotification(INotification notification);

	public void setServiceContext(ISkeletonContext actionContext);
}
