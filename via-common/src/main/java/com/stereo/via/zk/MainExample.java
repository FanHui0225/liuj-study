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
        CuratorFramework client = CuratorFrameworkFactory.newClient("zoo1:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        CrudExamples.delete(client,"/examples");
    }
}
