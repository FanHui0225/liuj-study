package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.handle.LocaleHandle;
import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.Locale;

/**
 * Serializing a locale.
 */
public class LocaleSerializer extends AbstractSerializer {
	private static LocaleSerializer SERIALIZER = new LocaleSerializer();

	public static LocaleSerializer create() {
		return SERIALIZER;
	}

	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
		if (obj == null)
			out.writeNull();
		else {
			Locale locale = (Locale) obj;

			out.writeObject(new LocaleHandle(locale.toString()));
		}
	}
}
