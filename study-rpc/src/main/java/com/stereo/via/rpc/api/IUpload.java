package com.stereo.via.rpc.api;

public interface IUpload {

	static final int FILE_SEGMENTED_BUF = 1 * 1024 * 1024;

	static final String FILE_UPLOAD_METHOD = "handleUpload";

	static final String FILE_UPLOAD_AVAILABLE_METHOD = "allowUpload";

	static final String FILE_UPLOAD_COMPLETED = "rpc-upload-completed";

	static final String FILE_UPLOAD_FAILED = "rpc-upload-failed";

	static final String FILE_UPLOAD_AUTH_FAILED = "rpc-upload-not-allow";

	static final String FILE_UPLOAD_AUTH_COMPLETED = "rpc-upload-allow";

	static final int FILE_UPLOAD_EOF_MARKER = -255;

	public boolean upload(IProgress listener) throws Exception;

	public boolean upload() throws Exception;

}
