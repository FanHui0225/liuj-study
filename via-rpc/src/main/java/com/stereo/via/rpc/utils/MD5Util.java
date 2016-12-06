package com.stereo.via.rpc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	protected static MessageDigest messageDigest = null;

	protected static final int StepMaxSize = 1024 * 1024;

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			nsaex.printStackTrace();
		}
	}

	/**
	 * 获取文件md5值
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String getFileMD5String(File file) throws Exception {
		InputStream fis = null;
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				messageDigest.update(buffer, 0, numRead);
			}
			fis.close();
			return bufferToHex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String getMD5String(File file) throws Exception {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		}

		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		try {
			long startPosition = 0L;
			long step = file.length() / StepMaxSize;

			if (step == 0) {
				MappedByteBuffer byteBuffer = ch.map(
						FileChannel.MapMode.READ_ONLY, 0, file.length());
				messageDigest.update(byteBuffer);
				return bufferToHex(messageDigest.digest());
			}

			for (int i = 0; i < step; i++) {
				MappedByteBuffer byteBuffer = ch.map(
						FileChannel.MapMode.READ_ONLY, startPosition,
						StepMaxSize);
				messageDigest.update(byteBuffer);
				startPosition += StepMaxSize;
			}

			if (startPosition == file.length()) {
				return bufferToHex(messageDigest.digest());
			}

			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
					startPosition, file.length() - startPosition);
			messageDigest.update(byteBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ch.close();
			in.close();
		}
		return bufferToHex(messageDigest.digest());
	}

	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
		messageDigest.update(bytes);
		return bufferToHex(messageDigest.digest());
	}

	public static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static boolean checkPassword(String password, String md5PwdStr) {
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getFileMD5String(new File("/home/stereo/批量部署.zip")));
	}
}