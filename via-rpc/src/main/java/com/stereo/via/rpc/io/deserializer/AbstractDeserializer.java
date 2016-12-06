package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;
import com.stereo.via.rpc.exc.ProtocolException;

import java.io.IOException;

/**
 * Deserializing an object.
 */
public class AbstractDeserializer implements Deserializer {
	public static final NullDeserializer NULL = new NullDeserializer();

	public Class<?> getType() {
		return Object.class;
	}

	public boolean isReadResolve() {
		return false;
	}

	public Object readObject(AbstractInput in) throws IOException {
		Object obj = in.readObject();

		String className = getClass().getName();

		if (obj != null)
			throw error(className + ": unexpected object "
					+ obj.getClass().getName() + " (" + obj + ")");
		else
			throw error(className + ": unexpected null value");
	}

	public Object readList(AbstractInput in, int length)
			throws IOException {
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	public Object readLengthList(AbstractInput in, int length)
			throws IOException {
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	public Object readMap(AbstractInput in) throws IOException {
		Object obj = in.readObject();

		String className = getClass().getName();

		if (obj != null)
			throw error(className + ": unexpected object "
					+ obj.getClass().getName() + " (" + obj + ")");
		else
			throw error(className + ": unexpected null value");
	}

	/**
	 * Creates the field array for a class. The default implementation returns a
	 * String[] array.
	 * 
	 * @param len
	 *            number of items in the array
	 * @return the new empty array
	 */
	public Object[] createFields(int len) {
		return new String[len];
	}

	/**
	 * Creates a field value class. The default implementation returns the
	 * String.
	 * 
	 * @param len
	 *            number of items in the array
	 * @return the new empty array
	 */
	public Object createField(String name) {
		return name;
	}

	@Override
	public Object readObject(AbstractInput in, String[] fieldNames)
			throws IOException {
		return readObject(in, (Object[]) fieldNames);
	}

	/**
	 * Reads an object instance from the input stream
	 */
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		throw new UnsupportedOperationException(toString());
	}

	protected ProtocolException error(String msg) {
		return new ProtocolException(msg);
	}

	protected String codeName(int ch) {
		if (ch < 0)
			return "end of file";
		else
			return "0x" + Integer.toHexString(ch & 0xff);
	}

	/**
	 * The NullDeserializer exists as a marker for the factory classes so they
	 * save a null result.
	 */
	static final class NullDeserializer extends AbstractDeserializer {
	}
}
