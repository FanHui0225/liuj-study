package com.stereo.via.rpc.io.deserializer;

import com.stereo.via.rpc.exc.IOExceptionWrapper;
import com.stereo.via.rpc.io.AbstractInput;

import java.io.IOException;
import java.util.*;

/**
 * Deserializing a JDK 1.2 Collection.
 */
public class CollectionDeserializer extends AbstractListDeserializer {
	private Class _type;

	public CollectionDeserializer(Class type) {
		_type = type;
	}

	public Class getType() {
		return _type;
	}

	public Object readList(AbstractInput in, int length)
			throws IOException {
		Collection list = createList();

		in.addRef(list);

		while (!in.isEnd())
			list.add(in.readObject());

		in.readEnd();

		return list;
	}

	public Object readLengthList(AbstractInput in, int length)
			throws IOException {
		Collection list = createList();

		in.addRef(list);

		for (; length > 0; length--)
			list.add(in.readObject());

		return list;
	}

	private Collection createList() throws IOException {
		Collection list = null;

		if (_type == null)
			list = new ArrayList();
		else if (!_type.isInterface()) {
			try {
				list = (Collection) _type.newInstance();
			} catch (Exception e) {
			}
		}

		if (list != null) {
		} else if (SortedSet.class.isAssignableFrom(_type))
			list = new TreeSet();
		else if (Set.class.isAssignableFrom(_type))
			list = new HashSet();
		else if (List.class.isAssignableFrom(_type))
			list = new ArrayList();
		else if (Collection.class.isAssignableFrom(_type))
			list = new ArrayList();
		else {
			try {
				list = (Collection) _type.newInstance();
			} catch (Exception e) {
				throw new IOExceptionWrapper(e);
			}
		}
		return list;
	}
}
