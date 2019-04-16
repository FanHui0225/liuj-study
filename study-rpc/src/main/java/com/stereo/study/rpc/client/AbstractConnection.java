package com.stereo.study.rpc.client;

import com.stereo.study.rpc.api.IConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


abstract public class AbstractConnection implements IConnection {

	public void addHeader(String key, String value) {
	}

	abstract public OutputStream getOutputStream() throws IOException;

	abstract public void sendRequest() throws IOException;

	abstract public int getStatusCode();

	abstract public String getStatusMessage();

	abstract public InputStream getInputStream() throws IOException;

	public String getContentEncoding() {
		return null;
	}

	public void close() throws IOException {
		destroy();
	}

	abstract public void destroy() throws IOException;
}
