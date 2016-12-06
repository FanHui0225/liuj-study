package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Deserializing a JDK 1.2 Collection.
 */
public class IteratorDeserializer extends AbstractListDeserializer {
	private static IteratorDeserializer _deserializer;

	public static IteratorDeserializer create() {
		if (_deserializer == null)
			_deserializer = new IteratorDeserializer();

		return _deserializer;
	}

	@Override
	public Object readList(AbstractInput in, int length)
			throws IOException {
		ArrayList list = new ArrayList();

		in.addRef(list);

		while (!in.isEnd())
			list.add(in.readObject());

		in.readEnd();

		return list.iterator();
	}
}
