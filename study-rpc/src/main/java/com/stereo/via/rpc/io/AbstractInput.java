package com.stereo.via.rpc.io;

import com.stereo.via.rpc.io.factory.SerializerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

abstract public class AbstractInput {

	private RemoteResolver resolver;
	private byte[] _buffer;

	public void init(InputStream is) {
	}

	abstract public String getMethod();

	public void setRemoteResolver(RemoteResolver resolver) {
		this.resolver = resolver;
	}

	public RemoteResolver getRemoteResolver() {
		return resolver;
	}

	public void setSerializerFactory(SerializerFactory ser) {
	}

	abstract public int readCall() throws IOException;

	public void skipOptionalCall() throws IOException {
	}

	abstract public String readHeader() throws IOException;

	abstract public String readMethod() throws IOException;

	public int readMethodArgLength() throws IOException {
		return -1;
	}

	abstract public void startCall() throws IOException;

	abstract public void completeCall() throws IOException;

	abstract public Object readReply(Class expectedClass) throws Throwable;

	abstract public void startReply() throws Throwable;

	public void startReplyBody() throws Throwable {
	}

	abstract public void completeReply() throws IOException;

	abstract public boolean readBoolean() throws IOException;

	abstract public void readNull() throws IOException;

	abstract public int readInt() throws IOException;

	abstract public long readLong() throws IOException;

	abstract public double readDouble() throws IOException;

	abstract public long readUTCDate() throws IOException;

	abstract public String readString() throws IOException;

	public org.w3c.dom.Node readNode() throws IOException {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	abstract public Reader getReader() throws IOException;

	abstract public InputStream readInputStream() throws IOException;

	public boolean readToOutputStream(OutputStream os) throws IOException {
		InputStream is = readInputStream();
		if (is == null)
			return false;

		if (_buffer == null)
			_buffer = new byte[256];

		try {
			int len;

			while ((len = is.read(_buffer, 0, _buffer.length)) > 0) {
				os.write(_buffer, 0, len);
			}
			return true;
		} finally {
			is.close();
		}
	}

	abstract public byte[] readBytes() throws IOException;

	abstract public int readBytes(byte[] buffer, int offset, int length)
			throws IOException;

	abstract public Object readObject(Class expectedClass) throws IOException;

	abstract public Object readObject() throws IOException;

	abstract public Object readRemote() throws IOException;

	abstract public Object readRef() throws IOException;

	abstract public int addRef(Object obj) throws IOException;

	abstract public void setRef(int i, Object obj) throws IOException;

	public void resetReferences() {
	}

	abstract public int readListStart() throws IOException;

	abstract public int readLength() throws IOException;

	abstract public int readMapStart() throws IOException;

	abstract public String readType() throws IOException;

	abstract public boolean isEnd() throws IOException;

	abstract public void readEnd() throws IOException;

	abstract public void readMapEnd() throws IOException;

	abstract public void readListEnd() throws IOException;

	public void close() throws IOException {
	}
}
