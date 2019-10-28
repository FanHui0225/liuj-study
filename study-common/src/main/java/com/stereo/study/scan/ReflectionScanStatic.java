package com.stereo.study.scan;

import com.google.common.base.Predicate;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuj-ai on 2018/3/13.
 */
public class ReflectionScanStatic implements ScanStaticStrategy {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ScanStaticStrategy.class);

    /**
     * 扫描想要的类
     */
    @Override
    public ScanStaticModel scan(List<String> packNameList) {

        // 基本信息
        ScanStaticModel scanModel = scanBasicInfo(packNameList);

        // 分析
        analysis(scanModel);

        return scanModel;
    }

    public static void main(String[] args) throws InterruptedException {
        Pattern pattern = Pattern.compile("\\*\\s+from\\s+[\\w\\[\\]]*\\.?[\\w\\[\\]]*\\.?\\[?(\\b\\w+)\\]?[\\r\\n\\s]*");
//        Matcher matcher = pattern.matcher("select * from a.b.c");
        String str = "multipart/form-data; boundary=\"(.*)\"";
        String sql = "multipart/form-data; boundary=\"25e00f16-5518-490e-9741-d431a261f123\"";
        System.out.println(Pattern.matches(sql,sql));
        Pattern p = Pattern.compile(str);
        Matcher matcher = p.matcher(sql);
        while (matcher.find()) {
            String string = matcher.group(1);
            System.out.println(string);
        }
    }

    /**
     * 通过扫描，获取反射对象
     */
    private Reflections getReflection(List<String> packNameList) {

        //
        // filter
        //
        FilterBuilder filterBuilder = new FilterBuilder().includePackage("com.baidu.disconf");

        for (String packName : packNameList) {
            filterBuilder = filterBuilder.includePackage(packName);
        }
        Predicate<String> filter = filterBuilder;

        //
        // urls
        //
        Collection<URL> urlTotals = new ArrayList<URL>();
        for (String packName : packNameList) {
            Set<URL> urls = ClasspathHelper.forPackage(packName);
            urlTotals.addAll(urls);
        }

        //
        Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
                .setScanners(new SubTypesScanner().filterResultsBy(filter),
                        new TypeAnnotationsScanner()
                                .filterResultsBy(filter),
                        new FieldAnnotationsScanner()
                                .filterResultsBy(filter),
                        new MethodAnnotationsScanner()
                                .filterResultsBy(filter),
                        new MethodParameterScanner()).setUrls(urlTotals));

        return reflections;
    }

    /**
     * 分析出一些关系 出来
     */
    private void analysis(ScanStaticModel scanModel) {

        // 分析出配置文件MAP
        analysis4DisconfFile(scanModel);
    }

    /**
     * 分析出配置文件与配置文件中的Field的Method的MAP
     */
    private void analysis4DisconfFile(ScanStaticModel scanModel) {

        Map<Class<?>, Set<Method>> disconfFileItemMap = new HashMap<Class<?>, Set<Method>>();

        //
        // 配置文件MAP
        //
        Set<Class<?>> classdata = scanModel.getDisconfFileClassSet();
        for (Class<?> classFile : classdata) {
            disconfFileItemMap.put(classFile, new HashSet<Method>());
        }

        //
        // 将配置文件与配置文件中的配置项进行关联
        //
        Set<Method> af1 = scanModel.getDisconfFileItemMethodSet();
        for (Method method : af1) {

            Class<?> thisClass = method.getDeclaringClass();

            if (disconfFileItemMap.containsKey(thisClass)) {
                Set<Method> fieldSet = disconfFileItemMap.get(thisClass);
                fieldSet.add(method);
                disconfFileItemMap.put(thisClass, fieldSet);

            } else {

                LOGGER.error("cannot find CLASS ANNOTATION " + DisconfFile.class.getName()
                        + " for disconf file item: " +
                        method.toString());
            }
        }

        //
        // 最后的校验
        //
        Iterator<Class<?>> iterator = disconfFileItemMap.keySet().iterator();
        while (iterator.hasNext()) {

            Class<?> classFile = iterator.next();

            // 校验是否所有配置文件都含有配置
            if (disconfFileItemMap.get(classFile).isEmpty()) {
                LOGGER.info("disconf file hasn't any items: " + classFile.getName());
                continue;
            }

            // 校验配置文件类型是否合适(目前只支持.properties类型)
            DisconfFile disconfFile = classFile.getAnnotation(DisconfFile.class);
            boolean fileTypeRight = ScanVerify.isDisconfFileTypeRight(disconfFile);
            if (!fileTypeRight) {
                LOGGER.warn("now do not support this file type" + disconfFile.toString());
                continue;
            }
        }

        // 设置
        scanModel.setDisconfFileItemMap(disconfFileItemMap);
    }

    /**
     * 扫描基本信息
     */
    private ScanStaticModel scanBasicInfo(List<String> packNameList) {

        ScanStaticModel scanModel = new ScanStaticModel();

        //
        // 扫描对象
        //
        Reflections reflections = getReflection(packNameList);
        scanModel.setReflections(reflections);

        //
        // 获取DisconfFile class
        //
        Set<Class<?>> classdata = reflections.getTypesAnnotatedWith(DisconfFile.class);
        scanModel.setDisconfFileClassSet(classdata);

        //
        // 获取DisconfFileItem method
        //
        Set<Method> af1 = reflections.getMethodsAnnotatedWith(DisconfFileItem.class);
        scanModel.setDisconfFileItemMethodSet(af1);

        //
        // 获取DisconfItem method
        //
        af1 = reflections.getMethodsAnnotatedWith(DisconfItem.class);
        scanModel.setDisconfItemMethodSet(af1);

        //
        // 获取DisconfActiveBackupService
        //
        classdata = reflections.getTypesAnnotatedWith(DisconfActiveBackupService.class);
        scanModel.setDisconfActiveBackupServiceClassSet(classdata);

        //
        // 获取DisconfUpdateService
        //
        classdata = reflections.getTypesAnnotatedWith(DisconfUpdateService.class);
        scanModel.setDisconfUpdateService(classdata);

        // update pipeline
        Set<Class<? extends IDisconfUpdatePipeline>> iDisconfUpdatePipeline = reflections.getSubTypesOf
                (IDisconfUpdatePipeline
                        .class);
        if (iDisconfUpdatePipeline != null && iDisconfUpdatePipeline.size() != 0) {
            scanModel.setiDisconfUpdatePipeline((Class<IDisconfUpdatePipeline>) iDisconfUpdatePipeline
                    .toArray()[0]);
        }

        return scanModel;
    }
}