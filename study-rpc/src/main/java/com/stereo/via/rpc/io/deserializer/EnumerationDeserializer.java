package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;
import java.util.Vector;

/**
 * Deserializing a JDK 1.2 Collection.
 */
public class EnumerationDeserializer extends AbstractListDeserializer {
	private static EnumerationDeserializer _deserializer;

	public static EnumerationDeserializer create() {
		if (_deserializer == null)
			_deserializer = new EnumerationDeserializer();

		return _deserializer;
	}

	public Object readList(AbstractInput in, int length)
			throws IOException {
		Vector list = new Vector();

		in.addRef(list);

		while (!in.isEnd())
			list.add(in.readObject());

		in.readEnd();

		return list.elements();
	}
}
