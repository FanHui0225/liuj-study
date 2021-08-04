package com.stereo.study.k8s;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.HostAlias;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by liuj-ai on 2021/7/19.
 */
public class KubernetesUtil {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesUtil.class);
    private static final String NAMESPACE = "this-is-a-test";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("kubeconfig", "D:\\workspace\\config");
        Yaml yaml = new Yaml(new Constructor(Pod.class));
        FileInputStream inputStream = new FileInputStream("C:\\Users\\liuj-ai\\Desktop\\linkis\\docker\\spark-engine-template.yaml");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        Pod pod = yaml.load(inputStreamReader);
        inputStream.close();
        String name = ("sparkengine-35629");
        pod.getMetadata().setName(name);
        pod.getMetadata().setNamespace("linkis");

        Container container = pod.getSpec().getContainers().get(0);
        container.setName(name);
        container.setImage("mirror-registry.glodon.com/pdc/spark-engine:0.9.4");
        container.setCommand(new ArrayList<>(Arrays.asList("/bin/sh", "-c", "while true;do echo hello测试开启引擎xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxPod;done")));
//        container.setCommand(new ArrayList<>(Arrays.asList("/bin/sh", "-c", "java -Xmx256m -Xms256m -server -XX:+UseG1GC -XX:MaxPermSize=64m -XX:PermSize=32m -Xloggc:/appcom/logs/dataworkcloud/lius-f/flinkxEngine/flinkxEngine/35629-gc.log20210726-18_02 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Dwds.linkis.configuration=linkis-engine.properties -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=35289 -Dhdp.version=3.1.4.0-315 -cp /usr/app/service/linkis-ujes-enginemanager-0.9.4.jar:/usr/app/service/conf:/usr/app/service/linkis-flinkx-engine-0.9.4.jar:/usr/app/service/*:/etc/hadoop/conf:/appcom/config/hbase-config:/etc/spark2/conf:/etc/hive/conf com.webank.wedatasphere.linkis.engine.DataWorkCloudEngineApplication --dwc-conf _req_entrance_instance=flinkxEntrance,pdc-hadoop-dev-08:9211 --dwc-conf wds.linkis.yarnqueue.memory.max=300G --dwc-conf wds.linkis.preheating.time=9:00 --dwc-conf wds.linkis.instance=10 --dwc-conf wds.linkis.tmpfile.clean.time=10:00 --dwc-conf ticketId=f2b4a76f-bfca-4493-bedf-abaf82fca09f --dwc-conf dwc.application.instance=pdc-hadoop-dev-08:9212 --dwc-conf creator=lius-f --dwc-conf wds.linkis.yarnqueue=default --dwc-conf wds.linkis.yarnqueue.cores.max=150 --dwc-conf wds.linkis.client.memory.max=20G --dwc-conf dwc.application.name=flinkxEngineManager --dwc-conf user=lius-f --spring-conf eureka.client.serviceUrl.defaultZone=http://pdc-hadoop-dev-08:20303/eureka/ --spring-conf logging.config=classpath:log4j2-engine.xml --spring-conf spring.profiles.active=engine --spring-conf server.port=35629 --spring-conf spring.application.name=flinkxEngin")));
        System.out.println(pod);

        Config kubeConfig = new ConfigBuilder().withMasterUrl("https://ha.api.k8s.com:6443").build();
        KubernetesClient k8s = new DefaultKubernetesClient(kubeConfig);
        k8s.pods().inNamespace(pod.getMetadata().getNamespace()).create(pod);

//        for (int i = 0; i < 30; i++) {
//            Pod pod2 = k8s.pods().inNamespace(pod.getMetadata().getNamespace()).withName(pod.getMetadata().getName()).get();
//            List<ContainerStatus> containerStatuses = pod2.getStatus().getContainerStatuses();
//            for (ContainerStatus status : containerStatuses) {
//                System.out.println(status.getName());
//                if (status.getName().equals(name)){
//                    System.out.println(status.getLastState().toString());
//                }
//            }
//            Thread.sleep(1000L);
//        }
//        LogWatch watch = null;
//        BufferedReader reader = null;
//        try {
//            watch = k8s.pods().inNamespace(pod.getMetadata().getNamespace()).withName(pod.getMetadata().getName()).tailingLines(10).watchLog();
//            InputStream in = watch.getOutput();
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line;
//            int c = 0;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//                c++;
//                if (c >= 10) break;
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        } finally {
//            if (reader != null) reader.close();
//            if (watch != null) watch.close();
//        }
        k8s.pods().inNamespace(pod.getMetadata().getNamespace()).withName(pod.getMetadata().getName()).delete();
        Pod pod2 = k8s.pods().inNamespace(pod.getMetadata().getNamespace()).withName(pod.getMetadata().getName()).get();
        System.out.println(pod2);
        k8s.close();

//        System.out.println(DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmm"));
        System.out.println(getK8SMemoryResource(0));
    }


    public static String getK8SMemoryResource(long size) {
        if (size < 1024) {
            return "1Mi";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return "1Mi";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "Mi";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "Gi";
        }
    }
}
