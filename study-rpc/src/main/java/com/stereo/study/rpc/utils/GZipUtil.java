package com.stereo.study.rpc.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.*;

/**
 * 压缩工具类
 */
public class GZipUtil {
	public static final int BUFFER = 1024;
	public static final String EXT = ".gz";
	public static final String ZIP_EXT = ".zip";

	/**
	 * 数据压缩
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] compress(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 压缩
		compress(bais, baos);

		byte[] output = baos.toByteArray();

		baos.flush();
		baos.close();

		bais.close();

		return output;
	}

	/**
	 * 文件压缩
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void compress(File file) throws Exception {
		compress(file, true);
	}

	/**
	 * 文件压缩
	 * 
	 * @param file
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public static void compress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);
		compress(fis, fos);
		fis.close();
		fos.flush();
		fos.close();
		if (delete) {
			file.delete();
		}
	}

	/**
	 * 数据压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static void compress(InputStream is, OutputStream os)
			throws Exception {

		GZIPOutputStream gos = new GZIPOutputStream(os);

		int count;
		byte data[] = new byte[BUFFER];
		while ((count = is.read(data, 0, BUFFER)) != -1) {
			gos.write(data, 0, count);
		}

		gos.finish();

		gos.flush();
		gos.close();
	}

	/**
	 * 文件压缩
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void compress(String path) throws Exception {
		compress(path, true);
	}

	/**
	 * 文件压缩
	 * 
	 * @param path
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public static void compress(String path, boolean delete) throws Exception {
		File file = new File(path);
		compress(file, delete);
	}

	/**
	 * 数据解压缩
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] decompress(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 解压缩

		decompress(bais, baos);

		data = baos.toByteArray();

		baos.flush();
		baos.close();

		bais.close();

		return data;
	}

	/**
	 * 文件解压缩
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void decompress(File file) throws Exception {
		decompress(file, true);
	}

	/**
	 * 文件解压缩
	 * 
	 * @param file
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public static void decompress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath().replace(EXT,
				""));
		decompress(fis, fos);
		fis.close();
		fos.flush();
		fos.close();

		if (delete) {
			file.delete();
		}
	}

	/**
	 * 数据解压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static void decompress(InputStream is, OutputStream os)
			throws Exception {

		GZIPInputStream gis = new GZIPInputStream(is);

		int count;
		byte data[] = new byte[BUFFER];
		while ((count = gis.read(data, 0, BUFFER)) != -1) {
			os.write(data, 0, count);
		}

		gis.close();
	}

	/**
	 * 文件解压缩
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void decompress(String path) throws Exception {
		decompress(path, true);
	}

	/**
	 * 文件解压缩
	 * 
	 * @param path
	 * @param delete
	 *            是否删除原始文件
	 * @throws Exception
	 */
	public static void decompress(String path, boolean delete) throws Exception {
		File file = new File(path);
		decompress(file, delete);
	}

	/**
	 * 多文件压缩
	 * 
	 * @param path
	 * @param delete
	 * @throws Exception
	 */
	public static void MultFileCompress(String path, boolean delete)
			throws Exception {
		File file = new File(path);
		MultFileCompress(file, delete);
	}

	/**
	 * 
	 * @param file
	 * @param delete
	 * @throws Exception
	 */
	public static void MultFileCompress(File file, boolean delete)
			throws Exception {
		byte[] buffer = new byte[BUFFER];
		String zipPath = file.getPath() + ZIP_EXT;
		List<File> files = FileUtil.getAllFileList(file);
		ZipOutputStream out = new ZipOutputStream(
				new FileOutputStream(zipPath), Charset.forName("UTF-8"));
		for (int i = 0; i < files.size(); i++) {
			FileInputStream in = new FileInputStream(files.get(i));
			out.putNextEntry(new ZipEntry(files.get(i).getName()));
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.closeEntry();
			in.close();
		}
		out.close();
		if (delete)
			FileUtil.delFolder(file.getPath());
	}

	/**
	 * 多文件解压缩
	 * 
	 * @param path
	 * @param delete
	 */
	public static void MultFileDeCompress(String path, boolean delete)
			throws Exception {
		File file = new File(path);
		MultFileDeCompress(file, delete);
	}

	/**
	 * @param file
	 * @param delete
	 */
	public static void MultFileDeCompress(File file, boolean delete)
			throws Exception {
		MultFileDeCompress(file, null, delete);
	}

	/**
	 * 
	 * @param file
	 * @param compress
	 * @param delete
	 * @throws Exception
	 */
	public static void MultFileDeCompress(File file, File compress,
			boolean delete) throws Exception {
		byte[] buffer = new byte[BUFFER];
		File zipPath = null;
		if (compress == null) {
			zipPath = new File(file.getPath().replace(ZIP_EXT, ""));
			zipPath.mkdir();
		} else {
			zipPath = compress;
			zipPath.mkdir();
		}
		ZipInputStream in = new ZipInputStream(new FileInputStream(file),
				Charset.forName("UTF-8"));
		ZipEntry zipEntry = null;
		while (null != (zipEntry = in.getNextEntry())) {
			int len;
			FileOutputStream out = new FileOutputStream(new File(
					zipPath.getPath() + "/" + zipEntry.getName()));
			while (-1 != (len = in.read(buffer))) {
				out.write(buffer, 0, len);
			}
			out.flush();
			out.close();
		}
		in.closeEntry();
		in.close();
		if (delete)
			file.delete();
	}

	public static void main(String args[]) throws Exception {
		MultFileDeCompress("/home/stereo/视频/demo.zip", true);
	}
}