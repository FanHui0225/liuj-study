package com.stereo.via.ipc.util;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class XmlUtils {

    public static String object2XmlWithoutXmlTitle(Class clazz, Object object) {
        StringBuffer xmlBuffer = new StringBuffer();
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            mar.marshal(object, writer);
            xmlBuffer.append(writer.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlBuffer.toString();
    }

    /**
     * 将对象转换成XML字符串
     */
    public static String object2Xml(Class clazz, Object object) {

        StringBuffer xmlBuffer = new StringBuffer();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            mar.marshal(object, writer);
            xmlBuffer.append(writer.toString());
            return xmlBuffer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将XML字符串转换成对象
     */
    public static <T> T xml2Object(Class<T> clazz, String context) throws JAXBException {

        T object = null;

        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            object = (T) u.unmarshal(new StringReader(context));
        } catch (JAXBException e) {
           throw e;
        }

        return object;
    }
    /**
     * 将XML字符串转换成对象(忽略命名空间)
     */
    public static <T> T xml2ObjectByRemoveNameSpace(Class<T> clazz, String context) throws Exception {

        T object = null;

        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(context);

            SAXParserFactory sax = SAXParserFactory.newInstance();
            sax.setNamespaceAware(false);
            XMLReader xmlReader = sax.newSAXParser().getXMLReader();

            Source source = new SAXSource(xmlReader, new InputSource(reader));
             object =(T) u.unmarshal(source);
        } catch (Exception e) {
            throw e;
        }

        return object;
    }


    /**
     * json转xml
     * @param xml
     * @return
     */
    public static String xml2JSON(String xml) {
        return new XMLSerializer().read(xml).toString();
    }

    /**
     * xml转json
     * @param json
     * @return
     */
    public static String json2XML(String json) {
        JSONObject jobj = JSONObject.fromObject(json);
        String xml = new XMLSerializer().write(jobj);
        return xml;
    }

    /**
     * 将XML字符串转换成对象(忽略命名空间)
     */
    public static <T> T xml2ObjectWithoutNameSpace(Class<T> clazz, String context) throws Exception {

        T object = null;

        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            StringReader reader = new StringReader(context);

            SAXParserFactory sax = SAXParserFactory.newInstance();
            sax.setNamespaceAware(false);
            XMLReader xmlReader = sax.newSAXParser().getXMLReader();

            Source source = new SAXSource(xmlReader, new InputSource(reader));
            object =(T) u.unmarshal(source);
        } catch (Exception e) {
            throw e;
        }

        return object;
    }

    public static String map2Xml(Map<String,String> map){
        StringBuffer sb = new StringBuffer();
        Set<String> set = map.keySet();
        for(Iterator<String> it = set.iterator(); it.hasNext();){
            String key = it.next();
            Object value = map.get(key);
            sb.append("<").append(key).append(">");
            sb.append(value);
            sb.append("</").append(key).append(">");
        }
        return sb.toString();
    }


    public static Map<String,Object> xml2Map(String xml){
        Map<String,Object> map = new HashMap<String,Object>();
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element el = doc.getRootElement();
            map = recGetXmlElementValue(el,map);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    @SuppressWarnings({ "unchecked" })
    private static Map<String, Object> recGetXmlElementValue(Element ele, Map<String, Object> map){
        List<Element> eleList = ele.elements();
        if (eleList.size() == 0) {
            map.put(ele.getName(), ele.getTextTrim());
            return map;
        } else {
            for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
                Element innerEle = iter.next();
                recGetXmlElementValue(innerEle, map);
            }
            return map;
        }
    }
}
