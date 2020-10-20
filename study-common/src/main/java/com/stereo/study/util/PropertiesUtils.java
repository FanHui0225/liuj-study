package com.stereo.study.util;

import java.io.IOException;
import java.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by liuj-ai on 2017/12/19.
 */
public final class PropertiesUtils
{
    private static final String DEFAULT_FIELD = "";
    private static final String SEPARATOR = ".";
    /**
     * json字段重复直接覆盖
     *
     * @param properties
     * @return
     */
    public static String toJson(Properties properties)
    {
        if (properties == null || properties.size() == 0)
        {
            return null;
        }else
        {
            JSONObject root = new JSONObject();
            properties.forEach((k,v) ->
            {                JSONObject tmpJsonObj;
                if ( k != null && v !=null )
                {
                    String key = String.valueOf(k);
                    String value = String.valueOf(v);
                    JSONObject node = root;
                    String[] fields;
                    if (key.indexOf(SEPARATOR)!=-1)
                    {
                        fields = key.split("\\"+SEPARATOR);
                    }else
                    {
                        fields = new String[]{key};
                    }
                    String lastField = fields[fields.length - 1];
                    for (int i = 0; i < fields.length; i++)
                    {
                        String field = fields[i];
                        if (!lastField.equals(field))
                        {
                            if (!node.containsKey(field))
                            {
                                tmpJsonObj = new JSONObject();
                                node.put(field, tmpJsonObj);
                                node = tmpJsonObj;
                            }else
                            {
                                Object obj = node.get(field);
                                if (obj instanceof JSONObject)
                                {
                                    node = (JSONObject)obj;
                                }
                                else
                                {
                                    tmpJsonObj = new JSONObject();
                                    tmpJsonObj.put(DEFAULT_FIELD, obj);
                                    node.put(field, tmpJsonObj);
                                    node = tmpJsonObj;
                                }
                            }
                        }else
                        {
                            if (node.containsKey(field))
                            {
                                Object obj = node.get(field);
                                if (obj instanceof JSONObject)
                                {
                                    tmpJsonObj = (JSONObject)obj;
                                    tmpJsonObj.put(DEFAULT_FIELD, value);
                                }
                                else
                                {
                                    node.put(field, value);
                                }
                            }else
                                node.put(field, value);
                        }
                    }
                }
            });
            return JSON.toJSONString(root, true);
        }
    }

    public static Properties toProperties(JSONObject root)
    {
        Map<String,String> propertiesMap = toProperties(root,null);
        if (propertiesMap!=null && propertiesMap.size() > 0)
        {
            Properties properties = new Properties();
            properties.putAll(propertiesMap);
            return properties;
        }else
            return null;
    }

    protected static Map<String,String> toProperties(JSONObject root, String fieldLink)
    {
        if (root == null)
            return null;
        else
        {
            Map<String,String> properties = new HashMap<>();
            root.forEach((field, value) ->
            {
                String fieldLinkTmp = fieldLink != null ? fieldLink + field : field;
                if (value instanceof JSONObject)
                {
                    properties.putAll(toProperties((JSONObject) value, fieldLinkTmp + SEPARATOR));
                }else
                {
                    fieldLinkTmp = fieldLinkTmp.endsWith(SEPARATOR) ? fieldLinkTmp.substring(0, fieldLinkTmp.length() - 1) : fieldLinkTmp;
                    properties.put(fieldLinkTmp, String.valueOf(value));
                }
            });
            return properties;
        }
    }

    public static void main(String[] args) throws IOException
    {

        Properties p = new Properties();
        p.put("a.b.c.d.e.f.g", "1");
        p.put("a.b.c.d.e.f", "2");
        p.put("a.b.c.d.e", "3");
        p.put("a.b.c.d", "4");
        p.put("a.b.c", "5");
        p.put("a.b", "6");
        p.put("a", "7");

        p.put("x.a.b", "8");
        p.put("x.a.b.c", "9");
        p.put("x.a.b.c.d", "10");
        long start = System.currentTimeMillis();
		String jsonStr = PropertiesUtils.toJson(p);
        System.out.println("消耗时间 : " + (System.currentTimeMillis() - start));
        System.out.println("================================== JSON ==================================");
        System.out.println(jsonStr);
        System.out.println();
        start = System.currentTimeMillis();
        p = PropertiesUtils.toProperties(JSONObject.parseObject(jsonStr));
        System.out.println("消耗时间 : " + (System.currentTimeMillis() - start));
        System.out.println("================================== Properties ==================================");
        for (Map.Entry<Object,Object> entry : p.entrySet())
        {
            System.out.println(entry.getKey()+"="+entry.getValue());
        }
    }
}