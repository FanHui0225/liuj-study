package com.stereo.via.dubbo.cebbank.dto;

import com.stereo.via.dubbo.cebbank.util.XmlUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stereo on 17-3-28.
 */
public class Tin extends HashMap<String,String> implements Serializable {


    private List<Data> datas = new ArrayList<Data>();

    public Tin(){}

    public Tin putKeyValue(String key, String value) {
        put(key, value);
        return this;
    }

    public Tin putData(Data data)
    {
        datas.add(data);
        return this;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<tin>");
        buffer.append(XmlUtils.map2Xml(this));
        if (datas.size() > 0)
            for (Data data : datas)
                buffer.append(data.toString());
        buffer.append("</tin>");
        return buffer.toString();
    }
}
