package com.stereo.via.zk;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by stereo on 17-1-10.
 */
public class MainExample
{

    private static Logger LOG = LoggerFactory.getLogger(MainExample.class);

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("10.129.56.219:2183", new ExponentialBackoffRetry(1000, 3));
        client.start();
        List<String> test = client.getChildren().forPath("/");
        System.out.println(test);
    }
}
