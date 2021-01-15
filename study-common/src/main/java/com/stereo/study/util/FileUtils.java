package com.stereo.study.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuj-ai on 2019/11/6.
 */
public class FileUtils {

    /**
     * 回车符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 得到USER_HOME目录
     *
     * @param base 用户目录下文件夹
     * @return 得到用户目录
     */
    public static File getUserHomeDir(String base) {
        String userhome = System.getProperty("user.home");
        File file = new File(userhome, base);
        if (file.exists()) {
            if (!file.isDirectory()) {
                // LOGGER.error("{} exists, but not directory", file.getAbsolutePath());
                throw new IllegalArgumentException(file.getAbsolutePath() + " exists, but not directory");
            }
        } else {
            file.mkdirs(); // 可能创建不成功
        }
        return file;
    }

    /**
     * 读取类相对路径内容
     *
     * @param clazz        文件
     * @param relativePath 相对路径
     * @param encoding     编码
     * @return 文件内容
     * @throws IOException 发送IO异常
     */
    public static String file2String(Class clazz, String relativePath, String encoding) throws IOException {
        InputStream is = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            is = clazz.getResourceAsStream(relativePath);
            reader = new InputStreamReader(is, encoding);
            bufferedReader = new BufferedReader(reader);
            StringBuilder context = new StringBuilder();
            String lineText;
            while ((lineText = bufferedReader.readLine()) != null) {
                context.append(lineText).append(LINE_SEPARATOR);
            }
            return context.toString();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 读取类相对路径内容
     *
     * @param file 文件
     * @return 文件内容（按行）
     * @throws IOException 发送IO异常
     */
    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<String>();
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            String lineText = null;
            while ((lineText = bufferedReader.readLine()) != null) {
                lines.add(lineText);
            }
            return lines;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 字符流写文件 较快
     *
     * @param file 文件
     * @param data 数据
     * @return 操作是否成功
     * @throws IOException 发送IO异常
     */
    public static boolean string2File(File file, String data) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, false);
            writer.write(data);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return true;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param filePath
     * @return
     */
    public static boolean cleanDirectory(String filePath) {
        return cleanDirectory(new File(filePath));
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 文件夹
     * @return 是否删除完成
     */
    public static boolean cleanDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    boolean success = cleanDirectory(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件,但不删除根文件夹
     *
     * @param folder 文件夹
     */
    public static void cleanFolder(File folder) {
        if (!folder.exists()) {
            return;
        }
        if (!folder.isDirectory()) {
            return;
        }
        String[] children = folder.list();
        for (int i = 0; i < children.length; i++) {
            File file = new File(folder, children[i]);
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        }
    }
}