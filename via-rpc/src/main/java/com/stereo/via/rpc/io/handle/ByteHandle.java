package com.stereo.via.rpc.io.handle;

import java.io.Serializable;

/**
 * Handle for Java Byte objects.
 */
public class ByteHandle implements Serializable {
	private byte _value;

	private ByteHandle() {
	}

	public ByteHandle(byte value) {
		_value = value;
	}

	public byte getValue() {
		return _value;
	}

	public Object readResolve() {
		return new Byte(_value);
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + _value + "]";
	}
}
