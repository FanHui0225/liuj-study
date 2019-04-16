package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.io.InputStream;

/**
 * Serializing a stream object.
 */
public class InputStreamSerializer extends AbstractSerializer {
	public InputStreamSerializer() {
	}

	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		InputStream is = (InputStream) obj;

		if (is == null)
			out.writeNull();
		else {
			out.writeByteStream(is);
		}
	}
}
