package com.stereo.via.xml;

import com.stereo.via.ipc.util.XmlUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by stereo on 17-3-28.
 */
public class Data extends HashMap<String,String> implements Serializable {

    public static String DATA_FIELD_NAME = "Data";

    public Data putKeyValue(String key, String value) {
        put(key, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<Data>");
        buffer.append(XmlUtils.map2Xml(this));
        buffer.append("</Data>");
        return buffer.toString();
    }
}
