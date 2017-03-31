package com.stereo.via.dubbo.cebbank.service.impl;

import com.stereo.via.dubbo.cebbank.dto.In;
import com.stereo.via.dubbo.cebbank.dto.Out;
import com.stereo.via.dubbo.cebbank.service.CebBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stereo on 17-3-31.
 */
public class CebBankServiceImpl implements CebBankService{

    private static Logger LOG = LoggerFactory.getLogger(CebBankServiceImpl.class);
    @Override
    public Out pay(In in) throws Exception {
        Out out = new Out("<?xml version=\"1.0\" encoding=\"GBK\"?><out><head><Version>1.0.1</Version><InstId>100000000000001</InstId><AnsTranCode>BJCEBQBIRes</AnsTranCode><TrmSeqNum>2010051000013010</TrmSeqNum></head><tout><billKey>123456</billKey><companyId>654321</companyId><item1></item1><item2></item2><item3></item3><item4></item4><item5></item5><item6></item6><item7></item7><totalNum></totalNum><Data><contractNo>123456</contractNo><customerName>张三</customerName><balance></balance><payAmount>2314</payAmount><beginDate></beginDate><endDate></endDate><filed1></filed1><filed2></filed2><filed3></filed3><filed4></filed4><filed5></filed5></Data></tout></out>");
        LOG.debug(in.toString());
        LOG.debug(out.toString());
        return out;
    }
}
