package com.stereo.via.xml;


import java.io.Serializable;

/**
 * Created by stereo on 17-3-28.
 */
public class Head implements Serializable{

    public static String HEAD_FIELD_NAME = "head";

    private String version;
    private String instId;
    private String ansTranCode;
    private String trmSeqNum;

    public Head(String version, String instId, String ansTranCode, String trmSeqNum) {
        this.version = version;
        this.instId = instId;
        this.ansTranCode = ansTranCode;
        this.trmSeqNum = trmSeqNum;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getAnsTranCode() {
        return ansTranCode;
    }

    public void setAnsTranCode(String ansTranCode) {
        this.ansTranCode = ansTranCode;
    }

    public String getTrmSeqNum() {
        return trmSeqNum;
    }

    public void setTrmSeqNum(String trmSeqNum) {
        this.trmSeqNum = trmSeqNum;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<head>");
        buffer.append("<Version>");
        buffer.append(String.valueOf(version));
        buffer.append("</Version>");

        buffer.append("<InstId>");
        buffer.append(String.valueOf(instId));
        buffer.append("</InstId>");

        buffer.append("<AnsTranCode>");
        buffer.append(String.valueOf(ansTranCode));
        buffer.append("</AnsTranCode>");


        buffer.append("<TrmSeqNum>");
        buffer.append(String.valueOf(trmSeqNum));
        buffer.append("</TrmSeqNum>");

        buffer.append("</head>");
        return buffer.toString();
    }
}
