
package com.stereo.via.event;

public interface EventHandler<T extends Event> {

  void handle(T event);

}
