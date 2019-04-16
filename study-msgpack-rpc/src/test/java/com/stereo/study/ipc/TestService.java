package com.stereo.study.ipc;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.stereo.study.ipc.server.api.INotification;
import com.stereo.study.ipc.server.skeleton.service.ServiceContext;
import com.stereo.study.ipc.server.skeleton.service.Service;
import com.stereo.study.metrics.Metrics;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by stereo on 16-8-9.
 */
public class TestService extends Service implements ITestService {
    private static Logger LOG = LoggerFactory.getLogger(TestService.class);
    private Counter counter;
    private Meter meter;
    private Histogram histogram;

    public TestService(Class<?> cls) {
        super(cls);
        try {
            Metrics metrics = Metrics.build("127.0.0.1", 8649, GMetric.UDPAddressingMode.MULTICAST, 1);
            counter = metrics.addCounter("TestService-(test1+test4-)");
            meter = metrics.addMeter("TestService-test2");
            histogram = metrics.addHistogram("TestService-test3");
            metrics.start();
        } catch (Exception e) {
            LOG.error("Metrics build error");
        }
    }

    @Override
    public void handleNotification(INotification notification) {
    }

    public Bean test1(Bean bean) {
        counter.inc();
        LOG.info("TestService.test1 " + bean + " ServiceContext.getChannelHandlerContext() " + ServiceContext.getChannelHandlerContext());
        return bean;
    }

    @Override
    public int test2(Bean bean) {
        meter.mark();
        LOG.info("TestService.test2 " + bean);
        return 1;
    }

    @Override
    public Integer test3() {
        Random random = new Random();
        histogram.update(random.nextInt(Integer.MAX_VALUE));
        LOG.info("TestService.test3");
        return 1;
    }

    @Override
    public void test4() {
        counter.dec();
        LOG.info("TestService.test4");
    }

    @Override
    public Map<String, Bean> test5(List<Bean> beens) {
        LOG.info("TestService.test5 " + beens);
        Map<String, Bean> map = new HashMap<String, Bean>();
        for (int i = 0; i < beens.size(); i++) {
            map.put("" + i, beens.get(i));
        }
        return map;
    }

    @Override
    public Bean2 test6(Bean bean) {
        LOG.info("TestService.test6 " + bean);
        return new Bean2();
    }
}
