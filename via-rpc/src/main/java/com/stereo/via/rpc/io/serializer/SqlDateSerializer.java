package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.Date;

/**
 * Serializing a sql date object.
 */
public class SqlDateSerializer extends AbstractSerializer {
	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		if (obj == null)
			out.writeNull();
		else {
			Class cl = obj.getClass();

			if (out.addRef(obj))
				return;

			int ref = out.writeObjectBegin(cl.getName());

			if (ref < -1) {
				out.writeString("value");
				out.writeUTCDate(((Date) obj).getTime());
				out.writeMapEnd();
			} else {
				if (ref == -1) {
					out.writeInt(1);
					out.writeString("value");
					out.writeObjectBegin(cl.getName());
				}

				out.writeUTCDate(((Date) obj).getTime());
			}
		}
	}
}
