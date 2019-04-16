package com.stereo.via.rpc.io.deserializer;

import java.math.BigDecimal;

/**
 * Deserializing a BigDecimal
 */
public class BigDecimalDeserializer extends AbstractStringValueDeserializer {
	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}

	@Override
	protected Object create(String value) {
		return new BigDecimal(value);
	}
}
