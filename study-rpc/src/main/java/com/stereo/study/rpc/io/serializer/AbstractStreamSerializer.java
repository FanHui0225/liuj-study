package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Serializing an object containing a byte stream.
 */
abstract public class AbstractStreamSerializer extends AbstractSerializer {
	/**
	 * Writes the object to the output stream.
	 */
	@Override
	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		if (out.addRef(obj)) {
			return;
		}

		int ref = out.writeObjectBegin(getClassName(obj));

		if (ref < -1) {
			out.writeString("value");

			InputStream is = null;

			try {
				is = getInputStream(obj);
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);
			}

			if (is != null) {
				try {
					out.writeByteStream(is);
				} finally {
					is.close();
				}
			} else {
				out.writeNull();
			}

			out.writeMapEnd();
		} else {
			if (ref == -1) {
				out.writeClassFieldLength(1);
				out.writeString("value");

				out.writeObjectBegin(getClassName(obj));
			}

			InputStream is = null;

			try {
				is = getInputStream(obj);
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);
			}

			try {
				if (is != null)
					out.writeByteStream(is);
				else
					out.writeNull();
			} finally {
				if (is != null)
					is.close();
			}
		}
	}

	protected String getClassName(Object obj) {
		return obj.getClass().getName();
	}

	abstract protected InputStream getInputStream(Object obj)
			throws IOException;
}
