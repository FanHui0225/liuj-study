package com.stereo.via.schedule.impl;

import com.stereo.via.schedule.ISchedulingService;
import org.quartz.utils.Key;

/**
 * Created by liujing on 2016/11/30.
 */
public class JobKeyImpl extends Key implements ISchedulingService.IJobKey {
    public JobKeyImpl(String name, String group) {
        super(name, group);
    }
}
