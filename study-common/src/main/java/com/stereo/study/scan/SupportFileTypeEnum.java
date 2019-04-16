package com.stereo.study.scan;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liuj-ai on 2018/3/13.
 */
public enum SupportFileTypeEnum {

    /**
     * 支持properties 特殊处理，支持非properties任意处理
     */
    PROPERTIES(0, "properties"), XML(1, "xml"), ANY(3, "*");

    private int type = 0;
    private String modelName = null;

    private SupportFileTypeEnum(int type, String modelName) {
        this.type = type;
        this.modelName = modelName;
    }

    /**
     * 根据文件名返回其文件后缀ENUM
     */
    public static SupportFileTypeEnum getByFileName(String fileName) {

        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isEmpty(extension)) {
            return SupportFileTypeEnum.ANY;
        }

        for (SupportFileTypeEnum supportFileTypeEnum : SupportFileTypeEnum.values()) {

            if (extension.equals(supportFileTypeEnum.modelName)) {
                return supportFileTypeEnum;
            }
        }

        return SupportFileTypeEnum.ANY;
    }

    public static SupportFileTypeEnum getByType(int type) {

        int index = 0;
        for (SupportFileTypeEnum supportFileTypeEnum : SupportFileTypeEnum.values()) {

            if (type == index) {
                return supportFileTypeEnum;
            }

            index++;
        }

        return null;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

}