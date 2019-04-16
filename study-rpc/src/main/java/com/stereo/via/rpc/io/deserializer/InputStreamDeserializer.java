package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;

/**
 * Serializing a stream object.
 */
public class InputStreamDeserializer extends AbstractDeserializer {
	public static final InputStreamDeserializer DESER = new InputStreamDeserializer();

	public InputStreamDeserializer() {
	}

	public Object readObject(AbstractInput in) throws IOException {
		return in.readInputStream();
	}
}
