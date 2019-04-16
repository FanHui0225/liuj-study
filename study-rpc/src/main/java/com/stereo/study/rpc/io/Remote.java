package com.stereo.study.rpc.io;

public class Remote implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5904384750331820021L;

	private String type;

	private String url;

	public Remote(String type, String url) {
		this.type = type;
		this.url = url;
	}

	public Remote() {
	}

	public String getType() {
		return type;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public int hashCode() {
		return url.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Remote))
			return false;
		Remote remote = (Remote) obj;
		return url.equals(remote.url);
	}

	public String toString() {
		return "Remote[" + url + "]";
	}
}
