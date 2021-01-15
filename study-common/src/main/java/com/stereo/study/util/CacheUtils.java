package com.stereo.study.util;

import java.io.File;

/**
 * Created by liuj-ai on 2020/3/14.
 */
public class CacheUtils {


    public static String buildCacheStoreFolder(String persistentStorePath) {
        return buildCacheStoreFolder(persistentStorePath, true);
    }


    public static String buildCacheStoreFolder(String persistentStorePath, boolean preClean) {
        if (persistentStorePath != null) {
            File file = new File(persistentStorePath);
            if (preClean) {
                FileUtils.cleanFolder(file);
            }
            if (file.exists()) {
                if (!file.isDirectory()) {
                    throw new IllegalArgumentException(file.getAbsolutePath() + " exists, but not directory");
                }
            } else {
                file.mkdirs();
            }
            return persistentStorePath;
        } else {
            File file = FileUtils.getUserHomeDir(".SentryCache");
            if (preClean) {
                FileUtils.cleanFolder(file);
            }
            return file.getAbsolutePath();
        }
    }
}
