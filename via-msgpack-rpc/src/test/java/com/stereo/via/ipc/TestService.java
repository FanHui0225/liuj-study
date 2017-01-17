package com.stereo.via.ipc;

import com.stereo.via.ipc.server.api.INotification;
import com.stereo.via.ipc.server.skeleton.RequestContext;
import com.stereo.via.ipc.server.skeleton.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stereo on 16-8-9.
 */
public class TestService extends Service implements ITestService {
    private static Logger LOG = LoggerFactory.getLogger(TestService.class);

    public TestService(Class<?> cls) {
        super(cls);
    }

    @Override
    public void handleNotification(INotification notification) {
    }

    public Bean test1(Bean bean){
        LOG.info("TestService.test1 " + bean + " RequestContext.getChannelHandlerContext() " +  RequestContext.getChannelHandlerContext());
        return bean;
    }

    @Override
    public int test2(Bean bean) {
        LOG.info("TestService.test2 " + bean);
        return 1;
    }

    @Override
    public Integer test3() {
        LOG.info("TestService.test3");
        return 1;
    }

    @Override
    public void test4() {
        LOG.info("TestService.test4");
    }

    @Override
    public Map<String, Bean> test5(List<Bean> beens) {
        LOG.info("TestService.test5 " + beens);
        Map<String,Bean> map = new HashMap<String,Bean>();
        for (int i = 0; i < beens.size(); i++) {
            map.put(""+i,beens.get(i));
        }
        return map;
    }

    @Override
    public Bean2 test6(Bean bean) {
        LOG.info("TestService.test6 " + bean);
        return new Bean2();
    }
}
