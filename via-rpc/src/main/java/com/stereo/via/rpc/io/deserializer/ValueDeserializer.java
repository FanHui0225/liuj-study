package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Deserializing a string valued object
 */
abstract public class ValueDeserializer extends AbstractDeserializer {
	public Object readMap(AbstractInput in) throws IOException {
		String initValue = null;

		while (!in.isEnd()) {
			String key = in.readString();

			if (key.equals("value"))
				initValue = in.readString();
			else
				in.readObject();
		}

		in.readMapEnd();

		return create(initValue);
	}

	public Object readObject(AbstractInput in, String[] fieldNames)
			throws IOException {
		String initValue = null;

		for (int i = 0; i < fieldNames.length; i++) {
			if ("value".equals(fieldNames[i]))
				initValue = in.readString();
			else
				in.readObject();
		}

		return create(initValue);
	}

	abstract Object create(String value) throws IOException;
}
