package com.stereo.study.rpc.io.deserializer;

import com.stereo.study.rpc.exc.RpcRuntimeException;

import javax.management.ObjectName;

/**
 * Deserializing an ObjectName
 */
public class ObjectNameDeserializer extends AbstractStringValueDeserializer {
	@Override
	public Class<?> getType() {
		return ObjectName.class;
	}

	@Override
	protected Object create(String value) {
		try {
			return new ObjectName(value);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RpcRuntimeException(e);
		}
	}
}