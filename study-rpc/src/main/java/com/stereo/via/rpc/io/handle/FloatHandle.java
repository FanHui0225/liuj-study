package com.stereo.via.rpc.io.handle;

import java.io.Serializable;

/**
 * Handle for Java Float objects.
 */
public class FloatHandle implements Serializable {
	private float _value;

	private FloatHandle() {
	}

	public FloatHandle(float value) {
		_value = value;
	}

	public float getValue() {
		return _value;
	}

	public Object readResolve() {
		return new Float(_value);
	}

	public String toString() {
		return getClass().getSimpleName() + "[" + _value + "]";
	}
}
