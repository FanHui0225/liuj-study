package com.stereo.study.rpc.core.context;


import com.stereo.study.rpc.api.IAction;

/**
 * Action虚类(可相互广播)
 * 
 * @author stereo
 */
public abstract class Action<K, T> extends Notifier implements IAction<T> {

	protected K service;

	protected String actionName = "actionName";

	@Override
	public void onRegister() {
	}

	@Override
	public void onRemove() {
	}

	@Override
	public String getActionName() {
		return actionName;
	}

	public Action(String actionName) {
		if (actionName != null)
			this.actionName = actionName;
	}

	public Action(Class<?> cls) {
		this(cls.getSimpleName());
	}

	@Override
	public IAction<T> resolveAction(String actionName) {
		if (this.actionName.equals(actionName))
			return this;
		return null;
	}

	public abstract void setService(K service);

}