package com.stereo.study.rpc.io.deserializer;

import com.stereo.study.rpc.io.AbstractInput;

import java.io.IOException;
import java.util.HashMap;

/**
 * Serializing an object for known object types.
 */
public class AbstractMapDeserializer extends AbstractDeserializer {

	public Class getType() {
		return HashMap.class;
	}

	public Object readObject(AbstractInput in) throws IOException {
		Object obj = in.readObject();

		if (obj != null)
			throw error("expected map/object at " + obj.getClass().getName()
					+ " (" + obj + ")");
		else
			throw error("expected map/object at null");
	}
}
