package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Deserializes a string-valued object like BigDecimal.
 */
abstract public class AbstractStringValueDeserializer extends
        AbstractDeserializer {
	abstract protected Object create(String value) throws IOException;

	@Override
	public Object readMap(AbstractInput in) throws IOException {
		String value = null;

		while (!in.isEnd()) {
			String key = in.readString();

			if (key.equals("value"))
				value = in.readString();
			else
				in.readObject();
		}

		in.readMapEnd();

		Object object = create(value);

		in.addRef(object);

		return object;
	}

	@Override
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		String[] fieldNames = (String[]) fields;

		String value = null;

		for (int i = 0; i < fieldNames.length; i++) {
			if ("value".equals(fieldNames[i]))
				value = in.readString();
			else
				in.readObject();
		}

		Object object = create(value);

		in.addRef(object);

		return object;
	}
}
