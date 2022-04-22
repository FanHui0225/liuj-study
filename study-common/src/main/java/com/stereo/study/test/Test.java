package com.stereo.study.test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by liuj-ai on 2022/3/8.
 */
public class Test {
    volatile long takeLogIndex = 0;
    volatile long putLogIndex = 0;
    volatile boolean completed = false;
    final String taskId = UUID.randomUUID().toString();
    static final String endMark = "ABCDEFG";

    Writer out;
    BufferedReader in;
    volatile long taskLogFileSize;
    final String taskLogFileName;
    final Supplier<File> logFile;
    final String lineSeparator;

    Test() {
        this.taskLogFileName = taskId + ".log";
        this.logFile = () -> new File("D:\\workspace\\tmp", taskLogFileName);
        this.lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));
    }

    private boolean canWrite() {
        if (out != null) {
            return true;
        }
        try {
            File logFile = this.logFile.get();
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
            taskLogFileSize = logFile.length();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return out != null;
    }

    private void writeLine(String message) {
        write(message, true);
    }

    private void write(String message, boolean forceFlush) {
        try {
            out.write(message);
            out.write(lineSeparator);
            taskLogFileSize += message.getBytes(StandardCharsets.UTF_8.name()).length;
            putLogIndex++;
            if (forceFlush || putLogIndex % 10 == 0) {
                out.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean canRead() {
        if (in != null) {
            return true;
        }
        File logFile = this.logFile.get();
        if (logFile.exists()) {
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return in != null;
        } else {
            return false;
        }
    }

    synchronized void putLog(String log) {
        if (canWrite()) {
            if (log.contains(endMark)) {
                this.completed = true;
            } else {
                writeLine(log);
            }
        }
    }

    boolean isCompleted() {
        return putLogIndex > 0 ? completed : true;
    }

    synchronized List<String> takeLogs() {
        if (canRead() && takeLogIndex < putLogIndex) {
            final int takeMaxLog = 3;
            String line;
            int takeCount = 0;
            final List<String> logList = new ArrayList<>();
            try {
                if (isCompleted()) {
                    while ((line = in.readLine()) != null) {
                        logList.add(line);
                        takeCount++;
                    }
                } else {
                    while (takeCount < takeMaxLog && (line = in.readLine()) != null) {
                        logList.add(line);
                        takeCount++;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                takeLogIndex += takeCount;
            }
            return logList;
        } else {
            return Collections.emptyList();
        }
    }

    private void clear(boolean force) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
            }
        }
        if (force) {
            File logPath = logFile.get();
            if (logPath.exists()) {
                logPath.delete();
            }
        }
    }

    synchronized void clear() {
        takeLogIndex = 0;
        putLogIndex = 0;
        this.clear(false);
    }

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 60; i++) {
                    test.putLog(" in log -> " + i);
                }
                test.putLog(endMark);
                System.out.println(" in end");
            }
        }).start();

        Thread.sleep(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (test.hasLogs() || !test.isCompleted()) {
                    for (String log : test.takeLogs()) {
                        System.out.println(" out log ->" + log);
                    }
                }
                System.out.println(" out end");
            }
        }).start();


    }

    boolean hasLogs() {
        return putLogIndex - takeLogIndex > 0;
    }
}
