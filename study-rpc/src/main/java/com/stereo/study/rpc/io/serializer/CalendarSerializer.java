package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.handle.CalendarHandle;

import java.util.Calendar;

/**
 * Serializing a calendar.
 */
public class CalendarSerializer extends AbstractSerializer {
	public static final Serializer SER = new CalendarSerializer();

	/**
	 * java.util.Calendar serializes to com.caucho.hessian.io.CalendarHandle
	 */
	@Override
	public Object writeReplace(Object obj) {
		Calendar cal = (Calendar) obj;

		return new CalendarHandle(cal.getClass(), cal.getTimeInMillis());
	}
}
