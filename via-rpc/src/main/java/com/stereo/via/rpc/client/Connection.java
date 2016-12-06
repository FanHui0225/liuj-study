package com.stereo.via.rpc.client;

import com.stereo.via.rpc.exc.ConnectionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Connection extends AbstractConnection {

	private URL _url;
	private URLConnection _conn;

	private int _statusCode;
	private String _statusMessage;

	private InputStream _inputStream;
	private InputStream _errorStream;

	Connection(URL url, URLConnection conn) {
		_url = url;
		_conn = conn;
	}

	@Override
	public void addHeader(String key, String value) {
		_conn.setRequestProperty(key, value);
	}

	public OutputStream getOutputStream() throws IOException {
		return _conn.getOutputStream();
	}

	public void sendRequest() throws IOException {
		if (_conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) _conn;
			_statusCode = 500;
			try {
				_statusCode = httpConn.getResponseCode();
			} catch (Exception e) {
			}

			parseResponseHeaders(httpConn);

			InputStream is = null;

			if (_statusCode != 200) {
				StringBuffer sb = new StringBuffer();
				int ch;

				try {
					is = httpConn.getInputStream();

					if (is != null) {
						while ((ch = is.read()) >= 0)
							sb.append((char) ch);
						is.close();
					}
					is = httpConn.getErrorStream();
					if (is != null) {
						while ((ch = is.read()) >= 0)
							sb.append((char) ch);
					}
					_statusMessage = sb.toString();
				} catch (FileNotFoundException e) {
					throw new ConnectionException(
							"Proxy cannot connect to '" + _url, e);
				} catch (IOException e) {
					if (is == null)
						throw new ConnectionException(
								_statusCode + ": " + e, e);
					else
						throw new ConnectionException(_statusCode + ": "
								+ sb, e);
				}
				if (is != null)
					is.close();
				throw new ConnectionException(_statusCode + ": "
						+ sb.toString());
			}
		}
	}

	protected void parseResponseHeaders(HttpURLConnection conn)
			throws IOException {
	}

	public int getStatusCode() {
		return _statusCode;
	}

	public String getStatusMessage() {
		return _statusMessage;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return _conn.getInputStream();
	}

	@Override
	public String getContentEncoding() {
		return _conn.getContentEncoding();
	}

	@Override
	public void close() {
		_inputStream = null;
	}

	@Override
	public void destroy() {
		close();
		URLConnection conn = _conn;
		_conn = null;
		if (conn instanceof HttpURLConnection)
			((HttpURLConnection) conn).disconnect();
	}
}