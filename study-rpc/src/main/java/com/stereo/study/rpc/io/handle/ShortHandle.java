package com.stereo.study.rpc.io.handle;

import java.io.Serializable;

/**
 * Handle for Java Short objects.
 */
public class ShortHandle implements Serializable {
	private short _value;

	private ShortHandle() {
	}

	public ShortHandle(short value) {
		_value = value;
	}

	public short getValue() {
		return _value;
	}

	public Object readResolve() {
		return new Short(_value);
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + _value + "]";
	}
}
