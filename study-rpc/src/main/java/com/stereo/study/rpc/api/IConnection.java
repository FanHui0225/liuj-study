package com.stereo.study.rpc.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 连接接口
 * 
 * @author stereo
 */
public interface IConnection {

	public void addHeader(String key, String value);

	public OutputStream getOutputStream() throws IOException;

	public void sendRequest() throws IOException;

	public int getStatusCode();

	public String getStatusMessage();

	public String getContentEncoding();

	public InputStream getInputStream() throws IOException;

	public void close() throws IOException;

	public void destroy() throws IOException;
}
