package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing an object for known object types.
 */
public class ByteArraySerializer extends AbstractSerializer implements
		ObjectSerializer {
	public static final ByteArraySerializer SER = new ByteArraySerializer();

	private ByteArraySerializer() {
	}

	@Override
	public Serializer getObjectSerializer() {
		return this;
	}

	@Override
	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		byte[] data = (byte[]) obj;

		if (data != null)
			out.writeBytes(data, 0, data.length);
		else
			out.writeNull();
	}
}
