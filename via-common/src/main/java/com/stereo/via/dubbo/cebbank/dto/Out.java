package com.stereo.via.dubbo.cebbank.dto;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.Serializable;

/**
 * Created by stereo on 17-3-28.
 */
public class Out implements Serializable {

    public static String OUT_FIELD_NAME = "out";

    private Head head;

    private Tout tout;

    private String encoding;

    public Out(){}

    public Out(String xml) throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        Element element = document.getRootElement();
        encoding = document.getXMLEncoding();
        parse(element);
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Tout getTout() {
        return tout;
    }

    public void setTout(Tout tout) {
        this.tout = tout;
    }

    private void parse(Element root) throws DocumentException {
        if (OUT_FIELD_NAME.equals(root.getName()))
        {
            Element head = root.element(Head.HEAD_FIELD_NAME);
            if (head!=null)
            {
                this.head = new Head(head);
            }else
                throw new RuntimeException("cebbank is not found head");
            Element tout = root.element(Tout.TOUT_FIELD_NAME);
            if (tout!=null)
            {
                this.tout = new Tout(tout);
            }else
                throw new RuntimeException("cebbank is not found tout");

        }else
            throw new RuntimeException("cebbank is not out");
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\""+ encoding+"\"?>");
        buffer.append("<out>");
        buffer.append(head.toString());
        buffer.append(tout.toString());
        buffer.append("</out>");
        return buffer.toString();
    }
}
