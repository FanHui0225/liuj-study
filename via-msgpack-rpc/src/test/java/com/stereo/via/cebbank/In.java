package com.stereo.via.cebbank;

import java.io.Serializable;

/**
 * Created by stereo on 17-3-28.
 */
public class In implements Serializable {

    public static String IN_FIELD_NAME = "in";

    private Head head;
    private Tin tin;
    private String encoding;

    public In(Head head, Tin tin, String encoding) {
        this.head = head;
        this.tin = tin;
        this.encoding = encoding;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Tin getTin() {
        return tin;
    }

    public void setTin(Tin tin) {
        this.tin = tin;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?cebbank version=\"1.0\" encoding=\""+ encoding+"\"?>");
        buffer.append("<in>");
        buffer.append(head);
        buffer.append(tin);
        buffer.append("</in>");
        return buffer.toString();
    }
}

