package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.exc.RpcRuntimeException;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Deserializing a string valued object
 */
public class StringValueDeserializer extends AbstractStringValueDeserializer {
	private Class _cl;
	private Constructor _constructor;

	public StringValueDeserializer(Class cl) {
		try {
			_cl = cl;
			_constructor = cl.getConstructor(new Class[] { String.class });
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class getType() {
		return _cl;
	}

	@Override
	protected Object create(String value) throws IOException {
		if (value == null)
			throw new IOException(_cl.getName() + " expects name.");

		try {
			return _constructor.newInstance(new Object[] { value });
		} catch (Exception e) {
			throw new RpcRuntimeException(_cl.getName() + ": value=" + value
					+ "\n" + e, e);
		}
	}
}
