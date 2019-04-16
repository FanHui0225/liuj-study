package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.exc.RpcRuntimeException;
import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.logging.Logger;

abstract public class AbstractSerializer implements Serializer {

	public static final NullSerializer NULL = new NullSerializer();

	protected static final Logger log = Logger
			.getLogger(AbstractSerializer.class.getName());

	@Override
	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		if (out.addRef(obj)) {
			return;
		}

		try {
			Object replace = writeReplace(obj);

			if (replace != null) {
				// out.removeRef(obj);

				out.writeObject(replace);

				out.replaceRef(replace, obj);

				return;
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// log.log(Level.FINE, e.toString(), e);
			throw new RpcRuntimeException(e);
		}

		Class<?> cl = getClass(obj);

		int ref = out.writeObjectBegin(cl.getName());

		if (ref < -1) {
			writeObject10(obj, out);
		} else {
			if (ref == -1) {
				writeDefinition20(cl, out);

				out.writeObjectBegin(cl.getName());
			}

			writeInstance(obj, out);
		}
	}

	protected Object writeReplace(Object obj) {
		return null;
	}

	protected Class<?> getClass(Object obj) {
		return obj.getClass();
	}

	protected void writeObject10(Object obj, AbstractOutput out)
			throws IOException {
		throw new UnsupportedOperationException(getClass().getName());
	}

	protected void writeDefinition20(Class<?> cl, AbstractOutput out)
			throws IOException {
		throw new UnsupportedOperationException(getClass().getName());
	}

	protected void writeInstance(Object obj, AbstractOutput out)
			throws IOException {
		throw new UnsupportedOperationException(getClass().getName());
	}

	/**
	 * The NullSerializer exists as a marker for the factory classes so they
	 * save a null result.
	 */
	static final class NullSerializer extends AbstractSerializer {
		public void writeObject(Object obj, AbstractOutput out)
				throws IOException {
			throw new IllegalStateException(getClass().getName());
		}
	}
}
