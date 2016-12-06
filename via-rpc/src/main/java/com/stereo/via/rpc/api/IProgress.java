package com.stereo.via.rpc.api;

/**
 * 进度器
 * 
 * @author stereo
 */
public interface IProgress {

	void update(long pBytesRead, long pContentLength, int pItems);
}
