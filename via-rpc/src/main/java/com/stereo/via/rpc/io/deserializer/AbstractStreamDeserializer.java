package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Deserializing a byte stream
 */
abstract public class AbstractStreamDeserializer extends AbstractDeserializer {
	abstract public Class<?> getType();

	@Override
	public Object readMap(AbstractInput in) throws IOException {
		Object value = null;

		while (!in.isEnd()) {
			String key = in.readString();

			if (key.equals("value"))
				value = readStreamValue(in);
			else
				in.readObject();
		}

		in.readMapEnd();

		return value;
	}

	@Override
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		String[] fieldNames = (String[]) fields;

		Object value = null;

		for (int i = 0; i < fieldNames.length; i++) {
			if ("value".equals(fieldNames[i])) {
				value = readStreamValue(in);
				in.addRef(value);
			} else {
				in.readObject();
			}
		}

		return value;
	}

	abstract protected Object readStreamValue(AbstractInput in)
			throws IOException;
}
