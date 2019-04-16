package com.stereo.study.rpc.io;

import java.io.IOException;

public interface RemoteResolver {
	public Object lookup(String type, String url) throws IOException;
}
