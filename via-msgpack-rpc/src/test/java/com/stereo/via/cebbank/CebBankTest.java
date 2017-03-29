package com.stereo.via.cebbank;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stereo on 17-3-28.
 */
public class CebBankTest {

    private static Logger LOG = LoggerFactory.getLogger(CebBankTest.class);

    @Test
    public void test () throws DocumentException {
//        生成
        Head Head = new Head("1.0.1","100000000000001","BJCEBRWKReq","2011051300074921");
        Data data = new Data().putKeyValue("key1","value1").putKeyValue("key2","value2").putKeyValue("key3","value3");
        Tin Tin = new Tin().putKeyValue("partnerCode","746").putKeyValue("operationDate","20110513").putData(data);
        In in = new In(Head,Tin,"GBK");
        LOG.debug(in.toString());

//        解析
        Out out = new Out("<?cebbank version=\"1.0\" encoding=\"ISO-8859-1\"?><out><head><Version>1.0.1</Version><InstId>100000000000001</InstId><AnsTranCode>BJCEBQBIRes</AnsTranCode><TrmSeqNum>2010051000013010</TrmSeqNum></head><tout><billKey>123456</billKey><companyId>654321</companyId><item1></item1><item2></item2><item3></item3><item4></item4><item5></item5><item6></item6><item7></item7><totalNum></totalNum><Data><contractNo>123456</contractNo><customerName>张三</customerName><balance></balance><payAmount>2314</payAmount><beginDate></beginDate><endDate></endDate><filed1></filed1><filed2></filed2><filed3></filed3><filed4></filed4><filed5></filed5></Data></tout></out>");
        LOG.debug(out.getHead().toString());
        LOG.debug(out.getTout().toString());
        LOG.debug(out.toString());
    }
}
