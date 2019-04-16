package com.stereo.study.rpc.core;

import com.stereo.study.rpc.api.IUpload;
import com.stereo.study.rpc.io.AbstractInput;
import com.stereo.study.rpc.io.AbstractOutput;

import java.io.Serializable;

/**
 * 
 * @author liujing
 * 
 * @param <E>
 */
public interface IHandler<E> extends Serializable {

	/**
	 * 请求处理
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	E handle(E e) throws Exception;

	/**
	 * 是否允许上传
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean allowUpload(String user, String passwd) throws Exception;

	/**
	 * 上传处理
	 * 
	 * @param upload
	 * @throws Exception
	 */
	void handleUpload(IUpload upload) throws Exception;

	/**
	 * 上传处理
	 * 
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	void handleUpload(AbstractInput in, AbstractOutput out) throws Exception;
}
