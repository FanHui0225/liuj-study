package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.io.handle.AnnotationInvocationHandler;
import com.stereo.via.rpc.exc.IOExceptionWrapper;
import com.stereo.via.rpc.exc.RpcRuntimeException;
import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Deserializing a java annotation for known object types.
 */
public class AnnotationDeserializer extends AbstractMapDeserializer {
	private static final Logger log = Logger
			.getLogger(AnnotationDeserializer.class.getName());

	private Class _annType;

	public AnnotationDeserializer(Class annType) {
		_annType = annType;
	}

	public Class getType() {
		return _annType;
	}

	public Object readMap(AbstractInput in) throws IOException {
		try {
			int ref = in.addRef(null);

			HashMap<String, Object> valueMap = new HashMap<String, Object>(8);

			while (!in.isEnd()) {
				String key = in.readString();
				Object value = in.readObject();

				valueMap.put(key, value);
			}

			in.readMapEnd();

			return Proxy.newProxyInstance(_annType.getClassLoader(),
					new Class[] { _annType }, new AnnotationInvocationHandler(
							_annType, valueMap));

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOExceptionWrapper(e);
		}
	}

	public Object readObject(AbstractInput in, Object[] fields)
			throws IOException {
		String[] fieldNames = (String[]) fields;

		try {
			in.addRef(null);

			HashMap<String, Object> valueMap = new HashMap<String, Object>(8);

			for (int i = 0; i < fieldNames.length; i++) {
				String name = fieldNames[i];

				valueMap.put(name, in.readObject());
			}

			return Proxy.newProxyInstance(_annType.getClassLoader(),
					new Class[] { _annType }, new AnnotationInvocationHandler(
							_annType, valueMap));

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RpcRuntimeException(_annType.getName() + ":" + e, e);
		}
	}
}