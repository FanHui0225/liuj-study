package com.stereo.via.xml;

/**
 * Created by stereo on 17-3-28.
 */
public class GDUtil {

    public static void main(String[] args) {


        Head Head = new Head("1.0.1","100000000000001","BJCEBRWKReq","2011051300074921");

        Data data = new Data().putKeyValue("key1","value1").putKeyValue("key2","value2").putKeyValue("key3","value3");
        Tin Tin = new Tin().putKeyValue("partnerCode","746").putKeyValue("operationDate","20110513").putData(data);
        In in = new In(Head,Tin,"GBK");
        System.out.println(in);
    }
}
