package com.stereo.via.ipc;

import com.stereo.via.ipc.client.ClientProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by stereo on 16-8-9.
 */
public class Client {

    //@Test
    public static void main(String[] params) throws Exception {
        //创建客户连接代理
        final ClientProxy clientProxy = new ClientProxy(new Config( "127.0.0.1" , 10092 ));
        //初始化连接
        clientProxy.init();
        //连接远程服务
        clientProxy.start();
        //6个线程调用远程接口
        ExecutorService executor = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            final int index = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //创建代理接口
                        ITestService testAction = clientProxy.create(ITestService.class);
                        while (true)
                        {
                            //调用服务接口
                            if(index == 0)
                            {
                                Bean rs = testAction.test1(new Bean());
                                System.out.println("test1 结果=" + rs);
                            }
                            else if (index == 1)
                            {
                                int rs = testAction.test2(new Bean());
                                System.out.println("test2 结果=" + rs);
                            }
                            else if (index == 2)
                            {
                                int rs = testAction.test3();
                                System.out.println("test3 结果=" + rs);
                            }
                            else if (index == 3)
                            {
                                testAction.test4();
                                System.out.println("test4 结果 无返回值");
                            }
                            else if (index == 4)
                            {
                                List<Bean> list = new ArrayList<Bean>();
                                list.add(new Bean());
                                Map<String,Bean> rs = testAction.test5(list);
                                System.out.println("test5 结果=" + rs);
                            }else if (index == 5)
                            {
                                Bean2 rs = testAction.test6(new Bean());
                                System.out.println("test6 结果=" + rs);
                            }
                            Thread.sleep(5000);
                            //break;
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated());
        //关闭客户端连接
        clientProxy.close();
    }
}
