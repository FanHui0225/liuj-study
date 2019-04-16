package com.stereo.study.rpc.io.deserializer;

import com.stereo.study.rpc.io.AbstractInput;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Deserializing a Java array
 */
public class ArrayDeserializer extends AbstractListDeserializer {
	private Class _componentType;
	private Class _type;

	public ArrayDeserializer(Class componentType) {
		_componentType = componentType;

		if (_componentType != null) {
			try {
				_type = Array.newInstance(_componentType, 0).getClass();
			} catch (Exception e) {
			}
		}

		if (_type == null)
			_type = Object[].class;
	}

	public Class getType() {
		return _type;
	}

	/**
	 * Reads the array.
	 */
	public Object readList(AbstractInput in, int length)
			throws IOException {
		if (length >= 0) {
			Object[] data = createArray(length);

			in.addRef(data);

			if (_componentType != null) {
				for (int i = 0; i < data.length; i++)
					data[i] = in.readObject(_componentType);
			} else {
				for (int i = 0; i < data.length; i++)
					data[i] = in.readObject();
			}

			in.readListEnd();

			return data;
		} else {
			ArrayList list = new ArrayList();

			in.addRef(list);

			if (_componentType != null) {
				while (!in.isEnd())
					list.add(in.readObject(_componentType));
			} else {
				while (!in.isEnd())
					list.add(in.readObject());
			}

			in.readListEnd();

			Object[] data = createArray(list.size());
			for (int i = 0; i < data.length; i++)
				data[i] = list.get(i);

			return data;
		}
	}

	/**
	 * Reads the array.
	 */
	public Object readLengthList(AbstractInput in, int length)
			throws IOException {
		Object[] data = createArray(length);

		in.addRef(data);

		if (_componentType != null) {
			for (int i = 0; i < data.length; i++)
				data[i] = in.readObject(_componentType);
		} else {
			for (int i = 0; i < data.length; i++)
				data[i] = in.readObject();
		}

		return data;
	}

	protected Object[] createArray(int length) {
		if (_componentType != null)
			return (Object[]) Array.newInstance(_componentType, length);
		else
			return new Object[length];
	}

	public String toString() {
		return "ArrayDeserializer[" + _componentType + "]";
	}
}
