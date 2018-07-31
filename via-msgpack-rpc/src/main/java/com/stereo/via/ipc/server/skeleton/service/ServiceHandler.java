package com.stereo.via.ipc.server.skeleton.service;

import com.stereo.via.event.Event;
import com.stereo.via.ipc.exc.ViaRuntimeException;
import com.stereo.via.ipc.server.api.ISkeletonContext;
import com.stereo.via.ipc.server.event.RequestEvent;
import com.stereo.via.ipc.server.event.ResponseEvent;
import com.stereo.via.ipc.util.Daemon;
import com.stereo.via.ipc.util.ThreadPoolUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stereo.via.ipc.Config;
import com.stereo.via.ipc.Constants;
import com.stereo.via.ipc.server.api.IServiceHandler;
import com.stereo.via.ipc.server.api.IServiceInvoker;
import com.stereo.via.ipc.server.event.enums.ServiceEnum;
import com.stereo.via.service.AbstractService;
import java.util.concurrent.*;

/**
 * Created by stereo on 16-8-18.
 */
public class ServiceHandler extends AbstractService implements IServiceHandler{

    private static Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

    private Config config;
    private ExecutorService handlerPool;
    private final IServiceInvoker serviceInvoker;

    public ServiceHandler(ISkeletonContext servicer, Config config)
    {
        super("ServiceHandler");
        serviceInvoker = new ServiceInvoker(servicer);
        this.config = config;
    }

    void initHandlerPool() {
        int minPoolSize;
        int aliveTime;
        int maxPoolSize = config.getBusinessPoolSize();
        if (Constants.THREADPOOL_TYPE_FIXED.equals(config.getBusinessPoolType())) {
            minPoolSize = maxPoolSize;
            aliveTime = 0;
        } else if (Constants.THREADPOOL_TYPE_CACHED.equals(config.getBusinessPoolType())) {
            minPoolSize = 20;
            maxPoolSize = Math.max(minPoolSize, maxPoolSize);
            aliveTime = 60000;
        } else {
            throw new ViaRuntimeException("HandlerPool-"+ config.getBusinessPoolType());
        }
        boolean isPriority = Constants.QUEUE_TYPE_PRIORITY.equals(config.getBusinessPoolQueueType());
        BlockingQueue<Runnable> configQueue = ThreadPoolUtils.buildQueue(config.getBusinessPoolQueueSize(), isPriority);
        Daemon.DaemonFactory threadFactory = new Daemon.DaemonFactory();
        RejectedExecutionHandler handler = new RejectedExecutionHandler() {
            private int i = 1;
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
                if (i++ % 7 == 0)
                {
                    i = 1;
                    LOG.warn("Task:{} has been reject for InvokerPool exhausted!" +
                                    " pool:{}, active:{}, queue:{}, taskcnt: {}",
                            new Object[]{
                                    runnable,
                                    executor.getPoolSize(),
                                    executor.getActiveCount(),
                                    executor.getQueue().size(),
                                    executor.getTaskCount()
                            });
                }
                throw new RejectedExecutionException("Biz thread pool of provider has bean exhausted");
            }
        };
        handlerPool = new ThreadPoolExecutor(minPoolSize, maxPoolSize,
                aliveTime, TimeUnit.MILLISECONDS,
                configQueue, threadFactory, handler);
    }

    void shutdown() {
        if(handlerPool!=null && !handlerPool.isShutdown())
            handlerPool.shutdown();
    }

    @Override
    public void handleRequest(RequestEvent request) throws Exception {
        ServiceContext.begin(request.getTarget(),request.getChannelHandlerContext());
        try
        {
            boolean succeed = serviceInvoker.invoke(new ServiceCall(request.getTarget()));
            if (succeed)
                replyResponse(new ResponseEvent(request.getTarget(),request.getChannelHandlerContext()));
            else
                LOG.error("handleRequest failed request : " + request.getTarget());
        }
        finally {
            ServiceContext.end();
        }
    }

    @Override
    public void replyResponse(ResponseEvent response) throws Exception {
        Channel channel = response.getChannelHandlerContext().channel();
        channel.writeAndFlush(response.getTarget()).sync();
    }

    @Override
    public IServiceInvoker getServiceInvoker() {
        return serviceInvoker;
    }

    @Override
    public void handle(final Event<ServiceEnum> event)
    {
        ServiceEnum type = event.getType();
        switch (type)
        {
            case REQUEST:
                handlerPool.submit(new Runnable()
                {
                    @Override
                    public void run() {
                        try {
                            handleRequest((RequestEvent) event);
                        } catch (Exception ex) {
                            LOG.error("HandleRequest error",ex);
                        }
                    }
                });
                break;
            case RESPONSE:
                break;
            default:
                break;
        }
    }

    @Override
    protected void serviceInit() throws Exception {
        initHandlerPool();
    }

    @Override
    protected void serviceStart() throws Exception {
    }

    @Override
    protected void serviceStop() throws Exception {
        shutdown();
    }
}