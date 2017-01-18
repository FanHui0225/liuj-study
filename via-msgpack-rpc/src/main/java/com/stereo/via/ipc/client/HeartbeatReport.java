package com.stereo.via.ipc.client;

import com.stereo.via.ipc.Constants;
import com.stereo.via.ipc.Heartbeat;
import com.stereo.via.ipc.Packet;
import com.stereo.via.ipc.exc.IpcRuntimeException;
import com.stereo.via.ipc.server.api.IFunction;
import com.stereo.via.ipc.util.Daemon;
import com.stereo.via.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by stereo on 17-1-18.
 */
public class HeartbeatReport extends AbstractService implements Runnable
{
    enum HeartBeatState
    {
        BORN,
        HEALTHY,
        RECVERY,
        CEASE
    }
    private static Logger LOG = LoggerFactory.getLogger(HeartbeatReport.class);
    private Daemon thread;
    private ClientProxy clientProxy;
    final int heartBeatRate;
    final int heartbeatQuantity;
    volatile boolean running;
    volatile Heartbeat heartbeat;
    volatile int wrapFailed;
    volatile int wrapSucceed;
    volatile HeartBeatState state;

    public HeartbeatReport(ClientProxy proxy) {
        super("HeartbeatReport");
        clientProxy = proxy;
        heartbeat = new Heartbeat(proxy.getClientId());
        heartBeatRate = clientProxy.getConfig().getHeartBeatRate();
        heartbeatQuantity = clientProxy.getConfig().getHeartBeatQuantity();
    }

    @Override
    public void run()
    {
        try
        {
            while (running)
            {
                try
                {
                    heatbeat();
                }catch (Exception ex)
                {
                    LOG.error(getName() + " heatbeat fail" );
                    ex.printStackTrace();
                }
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
        LOG.info(getName() + " no init");
    }

    @Override
    protected void serviceStart() throws Exception
    {
        running = true;
        thread = new Daemon(this);
        state = HeartBeatState.BORN;
        thread.start();
    }

    @Override
    protected void serviceStop() throws Exception
    {
        running = false;
        thread.interrupt();
        state = HeartBeatState.CEASE;
        reportHeartBeat(Constants.TYPE_HEARTBEAT_REQUEST_UNREGISTER);
    }

    void heatbeat()
    {
        LOG.info(getName() + " heatbeat");
        heartbeat.now();
        reportHeartBeat(Constants.TYPE_HEARTBEAT);
    }

    void reportHeartBeat(byte type)
    {
        if (state == HeartBeatState.RECVERY)
        {
            LOG.info(getName() + " recovering");
            return;
        }
        AsyncFuture<Packet> future = clientProxy.sendPacket(Packet.packetHeartBeat(heartbeat, type));
        try {
            heartbeat = future.get(clientProxy.getConfig().getReadTimeout(), TimeUnit.MILLISECONDS).getHeartbeat();
            wrapFailed = 0;
            wrapSucceed++;
            if(wrapSucceed > heartbeatQuantity)
                state = HeartBeatState.HEALTHY;
        } catch (Exception ex) {
            LOG.error(getName() + " reportHeartBeat ", ex);
            wrapSucceed = 0;
            wrapFailed++;
            if (wrapFailed > heartbeatQuantity)
            {
                state = HeartBeatState.RECVERY;
                //恢复机制
                throw new IpcRuntimeException(getName() + " reportHeartBeat fail",ex);
            }
        }
    }
}
