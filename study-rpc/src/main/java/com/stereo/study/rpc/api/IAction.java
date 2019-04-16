package com.stereo.study.rpc.api;

/**
 * 控制接口
 * 
 * @author liujing
 */
public interface IAction<T> extends INotifier {

	/**
	 * 注册触发
	 */
	public void onRegister();

	/**
	 * 销毁触发
	 */
	public void onRemove();

	/**
	 * 获取ActionName
	 * 
	 * @return
	 */
	public String getActionName();

	/**
	 * 适配Action
	 * 
	 * @param actionName
	 * @return
	 */
	public IAction<T> resolveAction(String actionName);

	/**
	 * Action之间广播
	 * 
	 * @param notification
	 */
	public void handleNotification(INotification notification);
}
