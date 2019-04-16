package com.stereo.via.ipc.server;

import com.stereo.via.ipc.server.api.IService;
import com.stereo.via.ipc.server.api.ISkeletonContext;

/**
 * Created by stereo on 16-8-11.
 */
public class IpcRegistry {

    private ISkeletonContext skeletonContext;

    public IpcRegistry(ISkeletonContext skeletonContext)
    {
        this.skeletonContext = skeletonContext;
    }

    public void registerService(IService service) {
        skeletonContext.registerService(service);
    }

    public IService retrieveService(String serviceName) {
        return skeletonContext.retrieveService(serviceName);
    }

    public IService removeService(String serviceName) {
        return skeletonContext.removeService(serviceName);
    }

    public boolean hasService(String serviceName) {
        return skeletonContext.hasService(serviceName);
    }

    public ISkeletonContext getSkeletonContext() {
        return skeletonContext;
    }
}
