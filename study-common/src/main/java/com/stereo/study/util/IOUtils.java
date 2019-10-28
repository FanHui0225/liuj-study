package com.stereo.study.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtils {
    private static final int BUFFER_SIZE = 1024 * 8;

    private IOUtils() {
    }

    /**
     * write.
     *
     * @param is InputStream instance.
     * @param os OutputStream instance.
     * @return count.
     * @throws IOException
     */
    public static long write(InputStream is, OutputStream os) throws IOException {
        return write(is, os, BUFFER_SIZE);
    }

    /**
     * write.
     *
     * @param is         InputStream instance.
     * @param os         OutputStream instance.
     * @param bufferSize buffer size.
     * @return count.
     * @throws IOException
     */
    public static long write(InputStream is, OutputStream os, int bufferSize) throws IOException {
        int read;
        long total = 0;
        byte[] buff = new byte[bufferSize];
        while (is.available() > 0) {
            read = is.read(buff, 0, buff.length);
            if (read > 0) {
                os.write(buff, 0, read);
                total += read;
            }
        }
        return total;
    }

    /**
     * read string.
     *
     * @param reader Reader instance.
     * @return String.
     * @throws IOException
     */
    public static String read(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        try {
            write(reader, writer);
            return writer.getBuffer().toString();
        } finally {
            writer.close();
        }
    }

    /**
     * write string.
     *
     * @param writer Writer instance.
     * @param string String.
     * @throws IOException
     */
    public static long write(Writer writer, String string) throws IOException {
        Reader reader = new StringReader(string);
        try {
            return write(reader, writer);
        } finally {
            reader.close();
        }
    }

    /**
     * write.
     *
     * @param reader Reader.
     * @param writer Writer.
     * @return count.
     * @throws IOException
     */
    public static long write(Reader reader, Writer writer) throws IOException {
        return write(reader, writer, BUFFER_SIZE);
    }

    /**
     * write.
     *
     * @param reader     Reader.
     * @param writer     Writer.
     * @param bufferSize buffer size.
     * @return count.
     * @throws IOException
     */
    public static long write(Reader reader, Writer writer, int bufferSize) throws IOException {
        int read;
        long total = 0;
        char[] buf = new char[BUFFER_SIZE];
        while ((read = reader.read(buf)) != -1) {
            writer.write(buf, 0, read);
            total += read;
        }
        return total;
    }

    /**
     * read lines.
     *
     * @param file file.
     * @return lines.
     * @throws IOException
     */
    public static String[] readLines(File file) throws IOException {
        if (file == null || !file.exists() || !file.canRead())
            return new String[0];

        return readLines(new FileInputStream(file));
    }

    /**
     * read lines.
     *
     * @param is input stream.
     * @return lines.
     * @throws IOException
     */
    public static String[] readLines(InputStream is) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line);
            return lines.toArray(new String[0]);
        } finally {
            reader.close();
        }
    }

    /**
     * write lines.
     *
     * @param os    output stream.
     * @param lines lines.
     * @throws IOException
     */
    public static void writeLines(OutputStream os, String[] lines) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
        try {
            for (String line : lines)
                writer.println(line);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    /**
     * write lines.
     *
     * @param file  file.
     * @param lines lines.
     * @throws IOException
     */
    public static void writeLines(File file, String[] lines) throws IOException {
        if (file == null)
            throw new IOException("File is null.");
        writeLines(new FileOutputStream(file), lines);
    }

    /**
     * append lines.
     *
     * @param file  file.
     * @param lines lines.
     * @throws IOException
     */
    public static void appendLines(File file, String[] lines) throws IOException {
        if (file == null)
            throw new IOException("File is null.");
        writeLines(new FileOutputStream(file, true), lines);
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream = new FileInputStream("D:\\日志分析\\uplinkinflux-test.2019-10-25.1.log")));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = Pattern.compile("(.*)write_db_loss:([0-9]+),\\s+(.*)").matcher(line);
            if (matcher.find()) {
                Integer c = Integer.valueOf(matcher.group(2));
                if (c > 50) {
                    System.out.println("耗时: " + c + "    " + line);
                }
            }
            if (line.contains("Started")) {
                System.out.println(line);
            }
        }
        reader.close();

//        long gzdtcount = 0;
//        long rootcount = 0;
//        long cqcount = 0;
//        long wcount = 0;
//        String start = "Sep 22 20:30";
////        String end = "Aug 19 08:50";
//        boolean sw = true;
////        BufferedWriter rootW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\liuj-ai\\Desktop\\influxdb\\influxdb_20190820\\root.log")));
////        BufferedWriter gzdtW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\liuj-ai\\Desktop\\influxdb\\influxdb_20190820\\gzdt.log")));
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\liuj-ai\\Desktop\\influxdb\\influxdb_20190922.txt")));
//        try {
//            String line;
//            while ((line = reader.readLine()) != null) {
////                if (line.startsWith(start))
////                    sw = true;
////                if (line.startsWith(end))
////                    sw = false;
//                if (sw) {
//                    if (line.contains("GET /query")) {
//                        String[] arr = line.split(" ");
//                        if (arr[8].equals("root")) {
//                            rootcount++;
////                            w(rootW, line);
//                        } else if (arr[8].equals("gzdt")) {
//                            gzdtcount++;
////                            w(gzdtW, line);
//                        }
////                        System.out.println(line);
//                    } else if (line.contains("Continuous query execution (start)")) {
////                        cqcount++;
////                        System.out.println(line);
//                    } else if (line.contains("POST /write")) {
//                        wcount++;
//                        String[] wArr = line.split(" ");
//                       long time = Long.valueOf(wArr[wArr.length - 1]);
//                       if (time/1000 >1000){
//                           System.out.println(line);
//                       }
//                    } else if (line.contains("Executing query")) {
//
//                        //continue;
//                    } else {
//                        // System.out.println(line);
//                    }
//                }
//            }
//            System.out.println("root查询总数:" + rootcount);
//            System.out.println("gzdt查询总数:" + gzdtcount);
//            System.out.println("cq查询总数:" + cqcount);
//            System.out.println("写入总数:" + wcount);
//
//        } finally {
//            reader.close();
////            rootW.close();
////            gzdtW.close();
//        }
    }

    public static void w(BufferedWriter writer, String line) throws IOException {
        String q = StringUtils.substringBetween(line, "q=", "&");
        if (q == null)
            q = StringUtils.substringBetween(line, "q=", " ");
        writer.write(URLDecoder.decode(q, "UTF-8"));
        writer.newLine();
        writer.flush();
    }
}