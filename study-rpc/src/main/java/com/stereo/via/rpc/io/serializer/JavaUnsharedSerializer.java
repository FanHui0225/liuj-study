package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Serializing an object for known object types.
 */
public class JavaUnsharedSerializer extends JavaSerializer {
	private static final Logger log = Logger
			.getLogger(JavaUnsharedSerializer.class.getName());

	public JavaUnsharedSerializer(Class<?> cl) {
		super(cl);
	}

	@Override
	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		boolean oldUnshared = out.setUnshared(true);

		try {
			super.writeObject(obj, out);
		} finally {
			out.setUnshared(oldUnshared);
		}
	}
}
