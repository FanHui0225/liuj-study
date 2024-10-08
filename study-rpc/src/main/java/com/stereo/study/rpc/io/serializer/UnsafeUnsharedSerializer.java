package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Serializing an object for known object types.
 */
public class UnsafeUnsharedSerializer extends UnsafeSerializer {
	private static final Logger log = Logger
			.getLogger(UnsafeUnsharedSerializer.class.getName());

	public UnsafeUnsharedSerializer(Class<?> cl) {
		super(cl);
	}

	@Override
	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		boolean oldUnshared = out.setUnshared(true);

		try {
			super.writeObject(obj, out);
		} finally {
			out.setUnshared(oldUnshared);
		}
	}
}
