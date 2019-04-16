package com.stereo.study.schedule.impl;

import com.stereo.study.schedule.ISchedulingService;
import org.quartz.utils.Key;

/**
 * Created by liujing on 2016/11/30.
 */
public class JobKeyImpl extends Key implements ISchedulingService.IJobKey {
    public JobKeyImpl(String name, String group) {
        super(name, group);
    }
}
