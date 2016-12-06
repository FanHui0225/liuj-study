package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.exc.IOExceptionWrapper;
import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Deserializing an enum valued object
 */
public class EnumDeserializer extends AbstractDeserializer {
	private Class _enumType;
	private Method _valueOf;

	public EnumDeserializer(Class cl) {
		if (cl.isEnum())
			_enumType = cl;
		else if (cl.getSuperclass().isEnum())
			_enumType = cl.getSuperclass();
		else
			throw new RuntimeException("Class " + cl.getName()
					+ " is not an enum");

		try {
			_valueOf = _enumType.getMethod("valueOf", new Class[] {
					Class.class, String.class });
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Class getType() {
		return _enumType;
	}

	public Object readMap(AbstractInput in) throws IOException {
		String name = null;

		while (!in.isEnd()) {
			String key = in.readString();

			if (key.equals("name"))
				name = in.readString();
			else
				in.readObject();
		}

		in.readMapEnd();

		Object obj = create(name);

		in.addRef(obj);

		return obj;
	}

	@Override
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		String[] fieldNames = (String[]) fields;
		String name = null;

		for (int i = 0; i < fieldNames.length; i++) {
			if ("name".equals(fieldNames[i]))
				name = in.readString();
			else
				in.readObject();
		}

		Object obj = create(name);

		in.addRef(obj);

		return obj;
	}

	private Object create(String name) throws IOException {
		if (name == null)
			throw new IOException(_enumType.getName() + " expects name.");

		try {
			return _valueOf.invoke(null, _enumType, name);
		} catch (Exception e) {
			throw new IOExceptionWrapper(e);
		}
	}
}
