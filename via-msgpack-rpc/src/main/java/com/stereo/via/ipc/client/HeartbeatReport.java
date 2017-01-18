package com.stereo.via.ipc.client;

import com.stereo.via.ipc.util.Daemon;
import com.stereo.via.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stereo on 17-1-18.
 */
public class HeartbeatReport extends AbstractService implements Runnable
{
    private static Logger LOG = LoggerFactory.getLogger(HeartbeatReport.class);
    private Daemon thread;
    private ClientProxy clientProxy;
    private final int heartBeatRate;
    private volatile boolean running;

    public HeartbeatReport(ClientProxy clientProxy) {
        super("HeartbeatReport");
        this.clientProxy = clientProxy;
        heartBeatRate = clientProxy.getConfig().getHeartBeatRate();
    }

    @Override
    public void run()
    {
        try
        {
            while (running)
            {
                heatbeat();
                Thread.sleep(heartBeatRate);
            }
        }catch (InterruptedException ex)
        {
            LOG.info(getName() + " thread interrupted");
        }
    }

    @Override
    protected void serviceInit() throws Exception
    {
    }

    @Override
    protected void serviceStart() throws Exception
    {
        register();
        running = true;
        thread = new Daemon(this);
        thread.start();
    }

    @Override
    protected void serviceStop() throws Exception
    {
        running = false;
        thread.interrupt();
        unregister();
    }

    void register()
    {
        LOG.info(getName() + " register");
    }

    void unregister()
    {
        LOG.info(getName() + " unregister");
    }

    void heatbeat()
    {
        LOG.info(getName() + " heatbeat");
    }
}
