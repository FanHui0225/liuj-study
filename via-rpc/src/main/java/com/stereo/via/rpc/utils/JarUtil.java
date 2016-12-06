package com.stereo.via.rpc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarUtil {

	public static File createTempJar(String root) throws IOException {
		if (!new File(root).exists())
			return null;

		// 创建manifest文件
		Manifest manifest = new Manifest();

		// 设置主属性
		manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
		// 创建临时文件
		final File jarFile = File.createTempFile("MR-", ".jar",
				new File(System.getProperty("java.io.tmpdir")));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				jarFile.delete();
			}
		});
		// 创建Jar文件输出流
		JarOutputStream out = new JarOutputStream(
				new FileOutputStream(jarFile), manifest);
		createTempJarInner(out, new File(root), "");

		out.flush();
		out.close();

		return jarFile;
	}

	// 遍历目录下文件
	private static void createTempJarInner(JarOutputStream out, File f,
			String base) throws IOException {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (base.length() > 0) {
				base = base + File.separator;
			}
			for (int i = 0; i < fl.length; i++) {
				createTempJarInner(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new JarEntry(base));
			FileInputStream in = new FileInputStream(f);
			byte[] buffer = new byte[1024];
			int n = in.read(buffer);
			while (n != -1) {
				out.write(buffer, 0, n);
				n = in.read(buffer);
			}
			in.close();
		}
	}

	public static void main(String[] args) throws IOException {
		String bin = JarUtil.class.getClassLoader().getResource("").getPath();
		File jarFile = createTempJar(bin);
		ClassLoader classLoader = JarUtil.class.getClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		for (;;)
			;
	}
}