package com.stereo.via.rpc.io.factory;

import com.stereo.via.rpc.io.serializer.Serializer;
import com.stereo.via.rpc.exc.ProtocolException;
import com.stereo.via.rpc.io.deserializer.Deserializer;

/**
 * 抽象序列化工厂
 * @author liujing
 */
abstract public class AbstractSerializerFactory {
	
	abstract public Serializer getSerializer(Class cl)
			throws ProtocolException;

	abstract public Deserializer getDeserializer(Class cl)
			throws ProtocolException;
}
