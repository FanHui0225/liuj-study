package com.stereo.study.rpc.api;

/**
 * 进度器
 * 
 * @author stereo
 */
public interface IProgress {

	void update(long pBytesRead, long pContentLength, int pItems);
}
