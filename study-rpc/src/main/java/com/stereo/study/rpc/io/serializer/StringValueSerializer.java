package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing a remote object.
 */
public class StringValueSerializer extends AbstractSerializer {
	public static final Serializer SER = new StringValueSerializer();

	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		if (obj == null)
			out.writeNull();
		else {
			if (out.addRef(obj))
				return;

			Class cl = obj.getClass();

			int ref = out.writeObjectBegin(cl.getName());

			if (ref < -1) {
				out.writeString("value");
				out.writeString(obj.toString());
				out.writeMapEnd();
			} else {
				if (ref == -1) {
					out.writeInt(1);
					out.writeString("value");
					out.writeObjectBegin(cl.getName());
				}

				out.writeString(obj.toString());
			}
		}
	}
}
