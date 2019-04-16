package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing an object.
 */
public interface Serializer {
	public void writeObject(Object obj, AbstractOutput out)
			throws IOException;
}
