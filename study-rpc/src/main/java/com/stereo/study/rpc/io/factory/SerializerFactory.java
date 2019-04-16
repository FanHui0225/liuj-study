package com.stereo.study.rpc.io.factory;

import com.stereo.study.rpc.io.deserializer.*;
import com.stereo.study.rpc.io.serializer.*;
import com.stereo.study.rpc.exc.ProtocolException;
import com.stereo.study.rpc.io.AbstractInput;
import com.stereo.study.rpc.io.Remote;
import com.stereo.study.rpc.io.handle.RPCHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 序列化工厂
 * 
 * @author liujing
 */
public class SerializerFactory extends AbstractSerializerFactory {
	private static final Logger log = Logger.getLogger(SerializerFactory.class
			.getName());

	private static final Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(
			BasicDeserializer.OBJECT);

	private static final ClassLoader _systemClassLoader;

	private static final HashMap _staticTypeMap;

	private static final WeakHashMap<ClassLoader, SoftReference<SerializerFactory>> _defaultFactoryRefMap = new WeakHashMap<ClassLoader, SoftReference<SerializerFactory>>();

	private ContextSerializerFactory _contextFactory;
	private WeakReference<ClassLoader> _loaderRef;

	protected Serializer _defaultSerializer;

	protected ArrayList _factories = new ArrayList();

	protected CollectionSerializer _collectionSerializer;
	protected MapSerializer _mapSerializer;

	private Deserializer _hashMapDeserializer;
	private Deserializer _arrayListDeserializer;
	private ConcurrentHashMap _cachedSerializerMap;
	private ConcurrentHashMap _cachedDeserializerMap;
	private HashMap _cachedTypeDeserializerMap;

	private boolean _isAllowNonSerializable;
	private boolean _isEnableUnsafeSerializer = (UnsafeSerializer.isEnabled() && UnsafeDeserializer
			.isEnabled());

	public SerializerFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public SerializerFactory(ClassLoader loader) {
		_loaderRef = new WeakReference<ClassLoader>(loader);
		_contextFactory = ContextSerializerFactory.create(loader);
	}

	public static SerializerFactory createDefault() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		synchronized (_defaultFactoryRefMap) {
			SoftReference<SerializerFactory> factoryRef = _defaultFactoryRefMap
					.get(loader);

			SerializerFactory factory = null;

			if (factoryRef != null)
				factory = factoryRef.get();

			if (factory == null) {
				factory = new SerializerFactory();

				factoryRef = new SoftReference<SerializerFactory>(factory);

				_defaultFactoryRefMap.put(loader, factoryRef);
			}

			return factory;
		}
	}

	public ClassLoader getClassLoader() {
		return _loaderRef.get();
	}

	/**
	 * Set true if the collection serializer should send the java type.
	 */
	public void setSendCollectionType(boolean isSendType) {
		if (_collectionSerializer == null)
			_collectionSerializer = new CollectionSerializer();

		_collectionSerializer.setSendJavaType(isSendType);

		if (_mapSerializer == null)
			_mapSerializer = new MapSerializer();

		_mapSerializer.setSendJavaType(isSendType);
	}

	/**
	 * Adds a factory.
	 */
	public void addFactory(AbstractSerializerFactory factory) {
		_factories.add(factory);
	}

	/**
	 * If true, non-serializable objects are allowed.
	 */
	public void setAllowNonSerializable(boolean allow) {
		_isAllowNonSerializable = allow;
	}

	/**
	 * If true, non-serializable objects are allowed.
	 */
	public boolean isAllowNonSerializable() {
		return _isAllowNonSerializable;
	}

	/**
	 * Returns the serializer for a class.
	 * 
	 * @param cl
	 *            the class of the object that needs to be serialized.
	 * 
	 * @return a serializer object for the serialization.
	 */
	public Serializer getObjectSerializer(Class<?> cl)
			throws ProtocolException {
		Serializer serializer = getSerializer(cl);

		if (serializer instanceof ObjectSerializer)
			return ((ObjectSerializer) serializer).getObjectSerializer();
		else
			return serializer;
	}

	/**
	 * Returns the serializer for a class.
	 * 
	 * @param cl
	 *            the class of the object that needs to be serialized.
	 * 
	 * @return a serializer object for the serialization.
	 */
	public Serializer getSerializer(Class cl) throws ProtocolException {
		Serializer serializer;

		if (_cachedSerializerMap != null) {
			serializer = (Serializer) _cachedSerializerMap.get(cl);

			if (serializer != null)
				return serializer;
		}

		serializer = loadSerializer(cl);

		if (_cachedSerializerMap == null)
			_cachedSerializerMap = new ConcurrentHashMap(8);

		_cachedSerializerMap.put(cl, serializer);

		return serializer;
	}

	protected Serializer loadSerializer(Class<?> cl)
			throws ProtocolException {

		Serializer serializer = null;

		for (int i = 0; _factories != null && i < _factories.size(); i++) {
			AbstractSerializerFactory factory;

			factory = (AbstractSerializerFactory) _factories.get(i);

			serializer = factory.getSerializer(cl);

			if (serializer != null)
				return serializer;
		}

		serializer = _contextFactory.getSerializer(cl.getName());

		if (serializer != null)
			return serializer;

		ClassLoader loader = cl.getClassLoader();

		if (loader == null)
			loader = _systemClassLoader;

		ContextSerializerFactory factory = null;

		factory = ContextSerializerFactory.create(loader);

		serializer = factory.getCustomSerializer(cl);

		if (serializer != null)
			return serializer;

		// if (RPCRemoteObject.class.isAssignableFrom(cl))
		// return new RemoteSerializer();

		else if (JavaSerializer.getWriteReplace(cl) != null) {
			Serializer baseSerializer = getDefaultSerializer(cl);

			return new WriteReplaceSerializer(cl, getClassLoader(),
					baseSerializer);
		}

		else if (Map.class.isAssignableFrom(cl)) {
			if (_mapSerializer == null)
				_mapSerializer = new MapSerializer();

			return _mapSerializer;
		} else if (Collection.class.isAssignableFrom(cl)) {
			if (_collectionSerializer == null) {
				_collectionSerializer = new CollectionSerializer();
			}

			return _collectionSerializer;
		}

		else if (cl.isArray())
			return new ArraySerializer();

		else if (Throwable.class.isAssignableFrom(cl))
			return new ThrowableSerializer(cl, getClassLoader());

		else if (InputStream.class.isAssignableFrom(cl))
			return new InputStreamSerializer();

		else if (Iterator.class.isAssignableFrom(cl))
			return IteratorSerializer.create();

		else if (Calendar.class.isAssignableFrom(cl))
			return CalendarSerializer.SER;

		else if (Enumeration.class.isAssignableFrom(cl))
			return EnumerationSerializer.create();

		else if (Enum.class.isAssignableFrom(cl))
			return new EnumSerializer(cl);

		else if (Annotation.class.isAssignableFrom(cl))
			return new AnnotationSerializer(cl);

		return getDefaultSerializer(cl);
	}

	/**
	 * Returns the default serializer for a class that isn't matched directly.
	 * Application can override this method to produce bean-style serialization
	 * instead of field serialization.
	 * 
	 * @param cl
	 *            the class of the object that needs to be serialized.
	 * 
	 * @return a serializer object for the serialization.
	 */
	protected Serializer getDefaultSerializer(Class cl) {
		if (_defaultSerializer != null)
			return _defaultSerializer;

		if (!Serializable.class.isAssignableFrom(cl)
				&& !_isAllowNonSerializable) {
			throw new IllegalStateException("Serialized class " + cl.getName()
					+ " must implement java.io.Serializable");
		}

		if (_isEnableUnsafeSerializer
				&& JavaSerializer.getWriteReplace(cl) == null) {
			return UnsafeSerializer.create(cl);
		} else
			return JavaSerializer.create(cl);
	}

	/**
	 * Returns the deserializer for a class.
	 * 
	 * @param cl
	 *            the class of the object that needs to be deserialized.
	 * 
	 * @return a deserializer object for the serialization.
	 */
	public Deserializer getDeserializer(Class cl) throws ProtocolException {
		Deserializer deserializer;

		if (_cachedDeserializerMap != null) {
			deserializer = (Deserializer) _cachedDeserializerMap.get(cl);

			if (deserializer != null)
				return deserializer;
		}

		deserializer = loadDeserializer(cl);

		if (_cachedDeserializerMap == null)
			_cachedDeserializerMap = new ConcurrentHashMap(8);

		_cachedDeserializerMap.put(cl, deserializer);

		return deserializer;
	}

	protected Deserializer loadDeserializer(Class cl)
			throws ProtocolException {
		Deserializer deserializer = null;

		for (int i = 0; deserializer == null && _factories != null
				&& i < _factories.size(); i++) {
			AbstractSerializerFactory factory;
			factory = (AbstractSerializerFactory) _factories.get(i);

			deserializer = factory.getDeserializer(cl);
		}

		if (deserializer != null)
			return deserializer;

		// XXX: need test
		deserializer = _contextFactory.getDeserializer(cl.getName());

		if (deserializer != null)
			return deserializer;

		ContextSerializerFactory factory = null;

		if (cl.getClassLoader() != null)
			factory = ContextSerializerFactory.create(cl.getClassLoader());
		else
			factory = ContextSerializerFactory.create(_systemClassLoader);

		deserializer = factory.getCustomDeserializer(cl);

		if (deserializer != null)
			return deserializer;

		if (Collection.class.isAssignableFrom(cl))
			deserializer = new CollectionDeserializer(cl);

		else if (Map.class.isAssignableFrom(cl)) {
			deserializer = new MapDeserializer(cl);
		} else if (Iterator.class.isAssignableFrom(cl)) {
			deserializer = IteratorDeserializer.create();
		} else if (Annotation.class.isAssignableFrom(cl)) {
			deserializer = new AnnotationDeserializer(cl);
		} else if (cl.isInterface()) {
			deserializer = new ObjectDeserializer(cl);
		} else if (cl.isArray()) {
			deserializer = new ArrayDeserializer(cl.getComponentType());
		} else if (Enumeration.class.isAssignableFrom(cl)) {
			deserializer = EnumerationDeserializer.create();
		} else if (Enum.class.isAssignableFrom(cl))
			deserializer = new EnumDeserializer(cl);

		else if (Class.class.equals(cl))
			deserializer = new ClassDeserializer(getClassLoader());

		else
			deserializer = getDefaultDeserializer(cl);

		return deserializer;
	}

	/**
	 * Returns a custom serializer the class
	 * 
	 * @param cl
	 *            the class of the object that needs to be serialized.
	 * 
	 * @return a serializer object for the serialization.
	 */
	protected Deserializer getCustomDeserializer(Class cl) {
		try {
			Class serClass = Class.forName(cl.getName(), false,
					cl.getClassLoader());
			Deserializer ser = (Deserializer) serClass.newInstance();
			return ser;
		} catch (ClassNotFoundException e) {
			log.log(Level.FINEST, e.toString(), e);

			return null;
		} catch (Exception e) {
			log.log(Level.FINE, e.toString(), e);

			return null;
		}
	}

	/**
	 * Returns the default serializer for a class that isn't matched directly.
	 * Application can override this method to produce bean-style serialization
	 * instead of field serialization.
	 * 
	 * @param cl
	 *            the class of the object that needs to be serialized.
	 * 
	 * @return a serializer object for the serialization.
	 */
	protected Deserializer getDefaultDeserializer(Class cl) {
		if (InputStream.class.equals(cl))
			return InputStreamDeserializer.DESER;

		if (_isEnableUnsafeSerializer) {
			return new UnsafeDeserializer(cl);
		} else
			return new JavaDeserializer(cl);
	}

	/**
	 * Reads the object as a list.
	 */
	public Object readList(AbstractInput in, int length, String type)
			throws ProtocolException, IOException {
		Deserializer deserializer = getDeserializer(type);

		if (deserializer != null)
			return deserializer.readList(in, length);
		else
			return new CollectionDeserializer(ArrayList.class).readList(in,
					length);
	}

	/**
	 * Reads the object as a map.
	 */
	public Object readMap(AbstractInput in, String type)
			throws ProtocolException, IOException {
		Deserializer deserializer = getDeserializer(type);

		if (deserializer != null)
			return deserializer.readMap(in);
		else if (_hashMapDeserializer != null)
			return _hashMapDeserializer.readMap(in);
		else {
			_hashMapDeserializer = new MapDeserializer(HashMap.class);

			return _hashMapDeserializer.readMap(in);
		}
	}

	/**
	 * Reads the object as a map.
	 */
	public Object readObject(AbstractInput in, String type, String[] fieldNames)
			throws ProtocolException, IOException {
		Deserializer deserializer = getDeserializer(type);

		if (deserializer != null)
			return deserializer.readObject(in, fieldNames);
		else if (_hashMapDeserializer != null)
			return _hashMapDeserializer.readObject(in, fieldNames);
		else {
			_hashMapDeserializer = new MapDeserializer(HashMap.class);

			return _hashMapDeserializer.readObject(in, fieldNames);
		}
	}

	/**
	 * Reads the object as a map.
	 */
	public Deserializer getObjectDeserializer(String type, Class cl)
			throws ProtocolException {
		Deserializer reader = getObjectDeserializer(type);

		if (cl == null || cl.equals(reader.getType())
				|| cl.isAssignableFrom(reader.getType())
				|| reader.isReadResolve()
				|| RPCHandle.class.isAssignableFrom(reader.getType())) {
			return reader;
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("expected deserializer '" + cl.getName() + "' at '" + type
					+ "' (" + reader.getType().getName() + ")");
		}

		return getDeserializer(cl);
	}

	/**
	 * Reads the object as a map.
	 */
	public Deserializer getObjectDeserializer(String type)
			throws ProtocolException {
		Deserializer deserializer = getDeserializer(type);

		if (deserializer != null)
			return deserializer;
		else if (_hashMapDeserializer != null)
			return _hashMapDeserializer;
		else {
			_hashMapDeserializer = new MapDeserializer(HashMap.class);

			return _hashMapDeserializer;
		}
	}

	/**
	 * Reads the object as a map.
	 */
	public Deserializer getListDeserializer(String type, Class cl)
			throws ProtocolException {
		Deserializer reader = getListDeserializer(type);

		if (cl == null || cl.equals(reader.getType())
				|| cl.isAssignableFrom(reader.getType())) {
			return reader;
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("expected '" + cl.getName() + "' at '" + type + "' ("
					+ reader.getType().getName() + ")");
		}

		return getDeserializer(cl);
	}

	/**
	 * Reads the object as a map.
	 */
	public Deserializer getListDeserializer(String type)
			throws ProtocolException {
		Deserializer deserializer = getDeserializer(type);

		if (deserializer != null)
			return deserializer;
		else if (_arrayListDeserializer != null)
			return _arrayListDeserializer;
		else {
			_arrayListDeserializer = new CollectionDeserializer(ArrayList.class);

			return _arrayListDeserializer;
		}
	}

	/**
	 * Returns a deserializer based on a string type.
	 */
	public Deserializer getDeserializer(String type)
			throws ProtocolException {
		if (type == null || type.equals(""))
			return null;

		Deserializer deserializer;

		if (_cachedTypeDeserializerMap != null) {
			synchronized (_cachedTypeDeserializerMap) {
				deserializer = (Deserializer) _cachedTypeDeserializerMap
						.get(type);
			}

			if (deserializer != null)
				return deserializer;
		}

		deserializer = (Deserializer) _staticTypeMap.get(type);
		if (deserializer != null)
			return deserializer;

		if (type.startsWith("[")) {
			Deserializer subDeserializer = getDeserializer(type.substring(1));

			if (subDeserializer != null)
				deserializer = new ArrayDeserializer(subDeserializer.getType());
			else
				deserializer = new ArrayDeserializer(Object.class);
		} else {
			try {
				Class cl = Class.forName(type, false, getClassLoader());
				deserializer = getDeserializer(cl);
			} catch (Exception e) {
				log.warning(type + "' is an unknown class in "
						+ getClassLoader() + ":\n" + e);

				log.log(Level.FINER, e.toString(), e);
			}
		}

		if (deserializer != null) {
			if (_cachedTypeDeserializerMap == null)
				_cachedTypeDeserializerMap = new HashMap(8);

			synchronized (_cachedTypeDeserializerMap) {
				_cachedTypeDeserializerMap.put(type, deserializer);
			}
		}

		return deserializer;
	}

	private static void addBasic(Class cl, String typeName, int type) {
		Deserializer deserializer = new BasicDeserializer(type);
		_staticTypeMap.put(typeName, deserializer);
	}

	static {
		_staticTypeMap = new HashMap();

		addBasic(void.class, "void", BasicSerializer.NULL);

		addBasic(Boolean.class, "boolean", BasicSerializer.BOOLEAN);
		addBasic(Byte.class, "byte", BasicSerializer.BYTE);
		addBasic(Short.class, "short", BasicSerializer.SHORT);
		addBasic(Integer.class, "int", BasicSerializer.INTEGER);
		addBasic(Long.class, "long", BasicSerializer.LONG);
		addBasic(Float.class, "float", BasicSerializer.FLOAT);
		addBasic(Double.class, "double", BasicSerializer.DOUBLE);
		addBasic(Character.class, "char", BasicSerializer.CHARACTER_OBJECT);
		addBasic(String.class, "string", BasicSerializer.STRING);
		addBasic(StringBuilder.class, "string", BasicSerializer.STRING_BUILDER);
		addBasic(Object.class, "object", BasicSerializer.OBJECT);
		addBasic(Date.class, "date", BasicSerializer.DATE);

		addBasic(boolean.class, "boolean", BasicSerializer.BOOLEAN);
		addBasic(byte.class, "byte", BasicSerializer.BYTE);
		addBasic(short.class, "short", BasicSerializer.SHORT);
		addBasic(int.class, "int", BasicSerializer.INTEGER);
		addBasic(long.class, "long", BasicSerializer.LONG);
		addBasic(float.class, "float", BasicSerializer.FLOAT);
		addBasic(double.class, "double", BasicSerializer.DOUBLE);
		addBasic(char.class, "char", BasicSerializer.CHARACTER);

		addBasic(boolean[].class, "[boolean", BasicSerializer.BOOLEAN_ARRAY);
		addBasic(byte[].class, "[byte", BasicSerializer.BYTE_ARRAY);
		addBasic(short[].class, "[short", BasicSerializer.SHORT_ARRAY);
		addBasic(int[].class, "[int", BasicSerializer.INTEGER_ARRAY);
		addBasic(long[].class, "[long", BasicSerializer.LONG_ARRAY);
		addBasic(float[].class, "[float", BasicSerializer.FLOAT_ARRAY);
		addBasic(double[].class, "[double", BasicSerializer.DOUBLE_ARRAY);
		addBasic(char[].class, "[char", BasicSerializer.CHARACTER_ARRAY);
		addBasic(String[].class, "[string", BasicSerializer.STRING_ARRAY);
		addBasic(Object[].class, "[object", BasicSerializer.OBJECT_ARRAY);

		Deserializer objectDeserializer = new JavaDeserializer(Object.class);
		_staticTypeMap.put("object", objectDeserializer);
		_staticTypeMap.put(Remote.class.getName(), RemoteDeserializer.DESER);

		ClassLoader systemClassLoader = null;
		try {
			systemClassLoader = ClassLoader.getSystemClassLoader();
		} catch (Exception e) {
		}

		_systemClassLoader = systemClassLoader;
	}
}
