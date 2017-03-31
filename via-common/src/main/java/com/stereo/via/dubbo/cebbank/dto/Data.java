package com.stereo.via.dubbo.cebbank.dto;

import com.stereo.via.dubbo.cebbank.util.XmlUtils;
import org.dom4j.Element;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by stereo on 17-3-28.
 */
public class Data extends HashMap<String,String> implements Serializable {

    public static String DATA_FIELD_NAME = "Data";

    public Data(){}

    public Data(Element element)
    {
        List<Element> eleList = element.elements();
        for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
            Element innerEle = iter.next();
            putKeyValue(innerEle.getName(),innerEle.getTextTrim());
        }
    }

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
