package com.stereo.via.dubbo.cebbank.test;

import com.stereo.via.dubbo.cebbank.dto.*;
import com.stereo.via.dubbo.cebbank.service.CebBankService;
import com.stereo.via.dubbo.cebbank.service.impl.CebBankServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by stereo on 17-3-31.
 */
public class CebBankClient {

    private static Logger LOG = LoggerFactory.getLogger(CebBankClient.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring-dubbbo-consumer.xml"});
        context.start();
        System.out.println("CebBankClient start...");

        Head Head = new Head("1.0.1","100000000000001","BJCEBRWKReq","2011051300074921");
        Data data = new Data().putKeyValue("key1","value1").putKeyValue("key2","value2").putKeyValue("key3","value3");
        Tin Tin = new Tin().putKeyValue("partnerCode","746").putKeyValue("operationDate","20110513").putData(data);
        In in = new In(Head,Tin,"GBK");

        while (true)
        {
            CebBankService cebBankService = (CebBankService) context.getBean("cebBankService");
            Out out = cebBankService.pay(in);
            LOG.debug(out.toString());
            Thread.sleep(2000);
        }
    }
}
