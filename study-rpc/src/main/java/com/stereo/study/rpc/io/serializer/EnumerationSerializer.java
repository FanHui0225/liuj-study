package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Serializing a JDK 1.2 Enumeration.
 */
public class EnumerationSerializer extends AbstractSerializer {
	private static EnumerationSerializer _serializer;

	public static EnumerationSerializer create() {
		if (_serializer == null)
			_serializer = new EnumerationSerializer();

		return _serializer;
	}

	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		Enumeration iter = (Enumeration) obj;

		boolean hasEnd = out.writeListBegin(-1, null);

		while (iter.hasMoreElements()) {
			Object value = iter.nextElement();

			out.writeObject(value);
		}

		if (hasEnd)
			out.writeListEnd();
	}
}
