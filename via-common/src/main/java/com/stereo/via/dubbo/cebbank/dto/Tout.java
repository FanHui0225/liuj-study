package com.stereo.via.dubbo.cebbank.dto;

import com.stereo.via.dubbo.cebbank.util.XmlUtils;
import org.dom4j.Element;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by stereo on 17-3-29.
 */
public class Tout extends HashMap<String,String> implements Serializable {

    public static String TOUT_FIELD_NAME = "tout";

    private List<Data> datas = new ArrayList<Data>();

    public Tout(){}

    public Tout(Element element)
    {
        List<Element> eleList = element.elements();
        for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();)
        {
            Element innerEle = iter.next();
            if (innerEle.getName().equals(Data.DATA_FIELD_NAME))
                datas.add(new Data(innerEle));
            else
            {
                putKeyValue(innerEle.getName(),innerEle.getTextTrim());
            }
        }
    }

    public Tout putKeyValue(String key, String value) {
        put(key, value);
        return this;
    }

    public List<Data> getDatas() {
        return datas;
    }


    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<tout>");
        buffer.append(XmlUtils.map2Xml(this));
        if (datas.size() > 0)
            for (Data data : datas)
                buffer.append(data.toString());
        buffer.append("</tout>");
        return buffer.toString();
    }
}
