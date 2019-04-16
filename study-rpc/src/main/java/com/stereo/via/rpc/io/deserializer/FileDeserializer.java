package com.stereo.via.rpc.io.deserializer;

import java.io.File;

/**
 * Deserializing a File
 */
public class FileDeserializer extends AbstractStringValueDeserializer {
	@Override
	public Class getType() {
		return File.class;
	}

	@Override
	protected Object create(String value) {
		return new File(value);
	}
}
