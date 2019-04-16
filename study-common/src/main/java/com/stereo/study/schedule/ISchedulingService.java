package com.stereo.study.schedule;

import java.util.Date;
import java.util.List;

/**
 * 调度业务接口
 * 
 * @author liujing
 */
public interface ISchedulingService {

	public static String BEAN_NAME = "schedulingService";

	public IJobKey addScheduledCronJob(String cronExpression, IScheduledJob job);

	public IJobKey addScheduledJob(int interval, IScheduledJob job);

	public IJobKey addScheduledOnceJob(long timeDelta, IScheduledJob job);

	public IJobKey addScheduledOnceJob(Date date, IScheduledJob job);

	public IJobKey addScheduledJobAfterDelay(int interval, IScheduledJob job,
                                             int delay);
	public void pauseScheduledJob(IJobKey jobKey);

	public void resumeScheduledJob(IJobKey jobKey);

	public void removeScheduledJob(IJobKey jobKey);

	public List<IJobKey> getScheduledJobKeys();

	public void init();
	public void destroy();

	public interface IJobKey
	{
		String getName();
		String getGroup();
	}
}
