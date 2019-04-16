package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing a remote object.
 */
public class ObjectHandleSerializer extends AbstractSerializer {
	public static final Serializer SER = new ObjectHandleSerializer();

	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		if (obj == null)
			out.writeNull();
		else {
			if (out.addRef(obj))
				return;

			int ref = out.writeObjectBegin("object");

			if (ref < -1) {
				out.writeMapEnd();
			} else {
				if (ref == -1) {
					out.writeInt(0);
					out.writeObjectBegin("object");
				}
			}
		}
	}
}
