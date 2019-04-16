package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing an object.
 */
public interface Serializer {
	public void writeObject(Object obj, AbstractOutput out)
			throws IOException;
}
