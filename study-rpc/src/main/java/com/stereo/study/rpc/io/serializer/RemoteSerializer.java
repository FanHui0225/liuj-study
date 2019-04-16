package com.stereo.study.rpc.io.serializer;

import com.stereo.study.rpc.io.AbstractOutput;

import java.io.IOException;

/**
 * Serializing a remote object.
 */
public class RemoteSerializer extends AbstractSerializer {
	public void writeObject(Object obj, AbstractOutput out)
			throws IOException {
//		RPCRemoteObject remoteObject = (RPCRemoteObject) obj;
//
//		out.writeObject(new RPCRemote(remoteObject.getHessianType(),
//				remoteObject.getHessianURL()));
	}
}
