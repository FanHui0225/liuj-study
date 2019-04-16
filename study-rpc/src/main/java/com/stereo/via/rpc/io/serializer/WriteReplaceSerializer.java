package com.stereo.via.rpc.io.serializer;

import com.stereo.via.rpc.io.AbstractOutput;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serializing an object for known object types.
 */
public class WriteReplaceSerializer extends AbstractSerializer {
	private static final Logger log = Logger
			.getLogger(WriteReplaceSerializer.class.getName());

	private static Object[] NULL_ARGS = new Object[0];

	private Object _writeReplaceFactory;
	private Method _writeReplace;
	private Serializer _baseSerializer;

	public WriteReplaceSerializer(Class<?> cl, ClassLoader loader,
			Serializer baseSerializer) {
		introspectWriteReplace(cl, loader);

		_baseSerializer = baseSerializer;
	}

	private void introspectWriteReplace(Class<?> cl, ClassLoader loader) {
		try {
			String className = cl.getName() + "HessianSerializer";

			Class<?> serializerClass = Class.forName(className, false, loader);

			Object serializerObject = serializerClass.newInstance();

			Method writeReplace = getWriteReplace(serializerClass, cl);

			if (writeReplace != null) {
				_writeReplaceFactory = serializerObject;
				_writeReplace = writeReplace;
			}
		} catch (ClassNotFoundException e) {
		} catch (Exception e) {
			log.log(Level.FINER, e.toString(), e);
		}

		_writeReplace = getWriteReplace(cl);
		if (_writeReplace != null)
			_writeReplace.setAccessible(true);
	}

	/**
	 * Returns the writeReplace method
	 */
	protected static Method getWriteReplace(Class cl, Class param) {
		for (; cl != null; cl = cl.getSuperclass()) {
			for (Method method : cl.getDeclaredMethods()) {
				if (method.getName().equals("writeReplace")
						&& method.getParameterTypes().length == 1
						&& param.equals(method.getParameterTypes()[0]))
					return method;
			}
		}

		return null;
	}

	/**
	 * Returns the writeReplace method
	 */
	protected static Method getWriteReplace(Class cl) {
		for (; cl != null; cl = cl.getSuperclass()) {
			Method[] methods = cl.getDeclaredMethods();

			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];

				if (method.getName().equals("writeReplace")
						&& method.getParameterTypes().length == 0)
					return method;
			}
		}

		return null;
	}

	public void writeObject(Object obj, AbstractOutput out) throws IOException {
		int ref = out.getRef(obj);

		if (ref >= 0) {
			out.writeRef(ref);

			return;
		}

		try {
			Object repl;

			repl = writeReplace(obj);

			if (obj == repl) {
				if (log.isLoggable(Level.FINE)) {
					log.fine(this
							+ ": Hessian writeReplace error.  The writeReplace method ("
							+ _writeReplace
							+ ") must not return the same object: " + obj);
				}

				_baseSerializer.writeObject(obj, out);

				return;
			}

			out.writeObject(repl);

			out.replaceRef(repl, obj);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Object writeReplace(Object obj) {
		try {
			if (_writeReplaceFactory != null)
				return _writeReplace.invoke(_writeReplaceFactory, obj);
			else
				return _writeReplace.invoke(obj);
		} catch (RuntimeException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
