package com.stereo.study.rpc.io.factory;

import com.stereo.study.rpc.io.serializer.Serializer;
import com.stereo.study.rpc.exc.ProtocolException;
import com.stereo.study.rpc.io.deserializer.Deserializer;

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
