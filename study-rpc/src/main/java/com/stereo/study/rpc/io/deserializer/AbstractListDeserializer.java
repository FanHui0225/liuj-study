package com.stereo.study.rpc.io.deserializer;

import com.stereo.study.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Deserializing a JDK 1.2 Collection.
 */
public class AbstractListDeserializer extends AbstractDeserializer {
	public Object readObject(AbstractInput in) throws IOException {
		Object obj = in.readObject();

		if (obj != null)
			throw error("expected list at " + obj.getClass().getName() + " ("
					+ obj + ")");
		else
			throw error("expected list at null");
	}
}
