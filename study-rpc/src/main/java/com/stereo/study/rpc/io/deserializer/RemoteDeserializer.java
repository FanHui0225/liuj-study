package com.stereo.study.rpc.io.deserializer;

import com.stereo.study.rpc.io.AbstractInput;
import com.stereo.study.rpc.io.Remote;
import com.stereo.study.rpc.io.RemoteResolver;

import java.util.logging.Logger;

/**
 * Serializing an object for known object types.
 */
public class RemoteDeserializer extends JavaDeserializer {
	private static final Logger log = Logger.getLogger(RemoteDeserializer.class
			.getName());

	public static final Deserializer DESER = new RemoteDeserializer();

	public RemoteDeserializer() {
		super(Remote.class);
	}

	@Override
	public boolean isReadResolve() {
		return true;
	}

	@Override
	protected Object resolve(AbstractInput in, Object obj)
			throws Exception {
		Remote remote = (Remote) obj;
		RemoteResolver resolver = in.getRemoteResolver();

		if (resolver != null) {
			Object proxy = resolver.lookup(remote.getType(), remote.getURL());

			return proxy;
		} else
			return remote;
	}
}
