package com.stereo.study.ipc;

import java.util.List;
import java.util.Map;

/**
 * Created by stereo on 16-8-9.
 */
public interface ITestService {

    public Bean test1(Bean bean);

    public int test2(Bean bean);

    public Integer test3();

    public void test4();

    public Map<String,Bean> test5(List<Bean> beens);

    public Bean2 test6(Bean bean);
}
