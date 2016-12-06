package com.stereo.via.rpc.io;

import java.io.IOException;

public class AbstractResolver implements RemoteResolver {
	
	public Object lookup(String type, String url) throws IOException {
		return new Remote(type, url);
	}
}
