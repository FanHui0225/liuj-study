package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.exc.IOExceptionWrapper;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.io.deserializer.AbstractDeserializer;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Deserializing a string valued object
 */
public class SqlDateDeserializer extends AbstractDeserializer {
	private Class _cl;
	private Constructor _constructor;

	public SqlDateDeserializer(Class cl) {
		try {
			_cl = cl;
			_constructor = cl.getConstructor(new Class[] { long.class });
		} catch (NoSuchMethodException e) {
			throw new RpcRuntimeException(e);
		}
	}

	public Class getType() {
		return _cl;
	}

	public Object readMap(AbstractInput in) throws IOException {
		int ref = in.addRef(null);

		long initValue = Long.MIN_VALUE;

		while (!in.isEnd()) {
			String key = in.readString();

			if (key.equals("value"))
				initValue = in.readUTCDate();
			else
				in.readString();
		}

		in.readMapEnd();

		Object value = create(initValue);

		in.setRef(ref, value);

		return value;
	}

	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		String[] fieldNames = (String[]) fields;

		int ref = in.addRef(null);

		long initValue = Long.MIN_VALUE;

		for (int i = 0; i < fieldNames.length; i++) {
			String key = fieldNames[i];

			if (key.equals("value"))
				initValue = in.readUTCDate();
			else
				in.readObject();
		}

		Object value = create(initValue);

		in.setRef(ref, value);

		return value;
	}

	private Object create(long initValue) throws IOException {
		if (initValue == Long.MIN_VALUE)
			throw new IOException(_cl.getName() + " expects name.");

		try {
			return _constructor
					.newInstance(new Object[] { new Long(initValue) });
		} catch (Exception e) {
			throw new IOExceptionWrapper(e);
		}
	}
}
