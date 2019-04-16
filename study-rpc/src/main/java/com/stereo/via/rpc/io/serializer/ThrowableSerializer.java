package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing an object for known object types.
 */
public class ThrowableSerializer extends JavaSerializer {
	public ThrowableSerializer(Class<?> cl, ClassLoader loader) {
		super(cl);
	}

	@Override
	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		Throwable e = (Throwable) obj;

		e.getStackTrace();

		super.writeObject(obj, out);
	}
}
