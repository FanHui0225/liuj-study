package com.stereo.via.ipc;


import com.stereo.via.ipc.server.IpcRegistry;
import com.stereo.via.ipc.server.IpcServer;

/**
 * Created by stereo on 16-8-9.
 */
public class Server {

    //@Test
    public static void main(String[] params) throws Exception {

        //创建服务
        IpcServer ipcServer = new IpcServer(new Config("127.0.0.1",10092));
        //初始化
        ipcServer.init();
        //启动
        ipcServer.start();
        //注册接口
        IpcRegistry ipcRegistry = ipcServer.getIpcRegistry();
        ipcRegistry.registerService(new TestService(ITestService.class));
        //关闭服务
        //ipcServer.close();
    }
}
