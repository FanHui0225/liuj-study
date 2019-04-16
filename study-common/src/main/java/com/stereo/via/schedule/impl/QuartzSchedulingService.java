package com.stereo.via.schedule.impl;

import com.stereo.via.schedule.IScheduledJob;
import com.stereo.via.schedule.ISchedulingService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Quartz任务调度
 * 
 * @author liujing
 */
public class QuartzSchedulingService implements ISchedulingService {

	protected static Logger log = LoggerFactory
			.getLogger(QuartzSchedulingService.class);

	protected AtomicLong jobDetailCounter = new AtomicLong(0);

	protected SchedulerFactory factory;

	protected Scheduler scheduler;

	protected String instanceId;

	public QuartzSchedulingService() {
	}

	public void init(){
		log.debug("QuartzSchedulingService init...");
		try {
			if (factory == null) {
				factory = new StdSchedulerFactory();
			}
			if (instanceId == null) {
				scheduler = factory.getScheduler();
			} else {
				scheduler = factory.getScheduler(instanceId);
			}
			if (scheduler != null) {
				scheduler.start();
			} else {
				log.error("QuartzSchedulingService 尚未启动");
			}
		} catch (SchedulerException ex)
		{
			log.error("QuartzSchedulingService 初始化失败",ex);
			throw new RuntimeException(ex);
		}
	}

	public void setFactory(SchedulerFactory factory) {
		this.factory = factory;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public IJobKey addScheduledCronJob(String cronExpression, IScheduledJob job) {
		JobKey jobKey = getJobKey();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobKey)).startNow().withSchedule(cronSchedule(cronExpression)).build();
		scheduleJob(jobKey, trigger, job);
		return jobKey(jobKey);
	}

	public IJobKey addScheduledJob(int interval, IScheduledJob job)
	{
		JobKey jobKey = getJobKey();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobKey)).startNow().
				withSchedule(simpleSchedule().withIntervalInMilliseconds(interval).repeatForever()).build();
		scheduleJob(jobKey, trigger, job);
		return jobKey(jobKey);
	}

	public IJobKey addScheduledOnceJob(Date date, IScheduledJob job)
	{
		JobKey jobKey = getJobKey();
		Date runTime = date;
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobKey)).startAt(runTime).build();
		scheduleJob(jobKey, trigger, job);
		return jobKey(jobKey);
	}

	public IJobKey addScheduledOnceJob(long timeDelta, IScheduledJob job)
	{
		return addScheduledOnceJob(new Date(System.currentTimeMillis()
				+ timeDelta), job);
	}

	public IJobKey addScheduledJobAfterDelay(int interval, IScheduledJob job,
			int delay)
	{
		JobKey jobKey = getJobKey();
		Date runTime = DateBuilder.futureDate(delay,DateBuilder.IntervalUnit.MILLISECOND);
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobKey)).startAt(runTime).
				withSchedule(simpleSchedule().withIntervalInMilliseconds(interval).repeatForever()).build();
		scheduleJob(jobKey, trigger, job);
		return jobKey(jobKey);
	}

	public List<IJobKey> getScheduledJobKeys()
	{
		List<IJobKey> result = new ArrayList<IJobKey>();
		if (scheduler != null) {
			try
			{
				for (String groupName : scheduler.getJobGroupNames()) {

					for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)))
					{
						final String jobName = jobKey.getName();
						final String jobGroup = jobKey.getGroup();
						result.add(jobKey(JobKey.jobKey(jobName,jobGroup)));
					}
				}
			} catch (SchedulerException ex)
			{
				log.error("QuartzSchedulingService.getScheduledJobNames",ex);
				throw new RuntimeException(ex);
			}
		} else {
			log.warn("没有调度");
		}
		if (result.size() > 0)
		{
			return result;
		}else
			return null;
	}

	public void pauseScheduledJob(IJobKey iJobKey)
	{
		try
		{
			scheduler.pauseJob(jobKey(iJobKey.getName(),iJobKey.getGroup()));
		} catch (SchedulerException ex) {
			log.error("QuartzSchedulingService.pauseScheduledJob",ex);
			throw new RuntimeException(ex);
		}
	}

	public void resumeScheduledJob(IJobKey iJobKey)
	{
		try
		{
			scheduler.resumeJob(jobKey(iJobKey.getName(),iJobKey.getGroup()));
		} catch (SchedulerException ex) {
			log.error("QuartzSchedulingService.resumeScheduledJob",ex);
			throw new RuntimeException(ex);
		}
	}


	public void removeScheduledJob(IJobKey iJobKey)
	{
		try {
			scheduler.deleteJob(jobKey(iJobKey.getName(),iJobKey.getGroup()));
		} catch (SchedulerException ex) {
			log.error("QuartzSchedulingService.removeScheduledJob",ex);
			throw new RuntimeException(ex);
		}
	}

	public void pauseScheduledTrigger(JobKey jobKey)
	{
		try {
			scheduler.pauseTrigger(getTriggerKey(jobKey));
		} catch (SchedulerException ex) {
			log.error("QuartzSchedulingService.pauseScheduledTrigger",ex);
			throw new RuntimeException(ex);
		}
	}

	public void resumeScheduledTrigger(JobKey jobKey)
	{
		try {
			scheduler.resumeTrigger(getTriggerKey(jobKey));
		} catch (SchedulerException ex) {
			log.error("QuartzSchedulingService.resumeScheduledTrigger",ex);
			throw new RuntimeException(ex);
		}
	}


	private void scheduleJob(JobKey jobKey ,Trigger trigger, IScheduledJob job)
	{
		if (scheduler != null)
		{
			JobDetail jobDetail = JobBuilder.newJob(QuartzSchedulingServiceJob.class).withIdentity(jobKey).build();
			jobDetail.getJobDataMap().put(
					QuartzSchedulingServiceJob.SCHEDULING_SERVICE, this);
			jobDetail.getJobDataMap().put(
					QuartzSchedulingServiceJob.SCHEDULED_JOB, job);
			try {
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (SchedulerException ex) {
				log.error("QuartzSchedulingService.scheduleJob",ex);
				throw new RuntimeException(ex);
			}
		} else {
			log.warn("没有调度");
		}
	}

	public void destroy()
	{
		if (scheduler != null) {
			try {
				log.debug("QuartzSchedulingService destroy...");
				scheduler.shutdown();
			}catch (Exception ex)
			{
				log.error("QuartzSchedulingService destroy",ex);
				throw new RuntimeException(ex);
			}
		}
	}

	public JobKey getJobKey()
	{
		long index = jobDetailCounter.getAndIncrement();
		String job = "ScheduledJob_" + index;
		String group = "ScheduledGroup_" + index;
		return JobKey.jobKey(job,group);
	}

	public TriggerKey getTriggerKey(JobKey jobKey)
	{
		String name = "Trigger_"+ jobKey.getName();
		String group = "Trigger_" + jobKey.getGroup();
		return TriggerKey.triggerKey(name,group);
	}

	public static final IJobKey jobKey(final JobKey jobKey)
	{
		return new JobKeyImpl(jobKey.getName(),jobKey.getGroup());
	}

	public static JobKey jobKey(String job,String group)
	{
		return JobKey.jobKey(job,group);
	}

	public static TriggerKey triggerKey(String name,String group)
	{
		return TriggerKey.triggerKey(name,group);
	}
}