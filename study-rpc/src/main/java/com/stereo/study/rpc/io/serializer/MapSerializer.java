package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Serializing a JDK 1.2 java.util.Map.
 */
public class MapSerializer extends AbstractSerializer {
	private boolean _isSendJavaType = true;

	/**
	 * Set true if the java type of the collection should be sent.
	 */
	public void setSendJavaType(boolean sendJavaType) {
		_isSendJavaType = sendJavaType;
	}

	/**
	 * Return true if the java type of the collection should be sent.
	 */
	public boolean getSendJavaType() {
		return _isSendJavaType;
	}

	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		if (out.addRef(obj))
			return;

		Map map = (Map) obj;

		Class<?> cl = obj.getClass();

		if (cl.equals(HashMap.class) || !(obj instanceof java.io.Serializable))
			out.writeMapBegin(null);
		else if (!_isSendJavaType) {
			// hessian/3a19
			for (; cl != null; cl = cl.getSuperclass()) {
				if (cl.equals(HashMap.class)) {
					out.writeMapBegin(null);
					break;
				} else if (cl.getName().startsWith("java.")) {
					out.writeMapBegin(cl.getName());
					break;
				}
			}

			if (cl == null)
				out.writeMapBegin(null);
		} else {
			out.writeMapBegin(cl.getName());
		}

		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			out.writeObject(entry.getKey());
			out.writeObject(entry.getValue());
		}
		out.writeMapEnd();
	}
}
