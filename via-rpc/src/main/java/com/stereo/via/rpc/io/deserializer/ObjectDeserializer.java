package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Serializing an object for known object types.
 */
public class ObjectDeserializer extends AbstractDeserializer {
	private Class<?> _cl;

	public ObjectDeserializer(Class<?> cl) {
		_cl = cl;
	}

	public Class<?> getType() {
		return _cl;
	}

	@Override
	public Object readObject(AbstractInput in) throws IOException {
		return in.readObject();
	}

	@Override
	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	@Override
	public Object readList(AbstractInput in, int length)
			throws IOException {
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	@Override
	public Object readLengthList(AbstractInput in, int length)
			throws IOException {
		throw new UnsupportedOperationException(String.valueOf(this));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _cl + "]";
	}
}
