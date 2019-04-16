package com.stereo.study.event;

public interface Dispatcher {
	
	EventHandler getEventHandler();

	void register(Class<? extends Enum> eventType, EventHandler handler);
	
	void serviceStart() throws Exception;
	
	void serviceStop() throws Exception ;
}
