package com.stereo.via.ipc.server;

import com.stereo.via.ipc.server.api.IService;
import com.stereo.via.ipc.server.api.IServiceContext;

/**
 * Created by stereo on 16-8-11.
 */
public class IpcRegistry {

    private IServiceContext serviceContext;

    public IpcRegistry(IServiceContext serviceContext)
    {
        this.serviceContext = serviceContext;
    }

    public void registerService(IService service) {
        serviceContext.registerService(service);
    }

    public IService retrieveService(String serviceName) {
        return serviceContext.retrieveService(serviceName);
    }

    public IService removeService(String serviceName) {
        return serviceContext.removeService(serviceName);
    }

    public boolean hasService(String serviceName) {
        return serviceContext.hasService(serviceName);
    }

}
