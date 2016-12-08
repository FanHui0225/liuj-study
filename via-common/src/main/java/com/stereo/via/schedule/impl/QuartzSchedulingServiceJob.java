package com.stereo.via.schedule.impl;

import com.stereo.via.schedule.IScheduledJob;
import com.stereo.via.schedule.ISchedulingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 作业类
 * 
 * @author liujing
 */
public class QuartzSchedulingServiceJob implements Job {

	protected static final String SCHEDULING_SERVICE = "scheduling_service";

	protected static final String SCHEDULED_JOB = "scheduled_job";

	private Logger log = LoggerFactory
			.getLogger(QuartzSchedulingServiceJob.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ISchedulingService service = (ISchedulingService) arg0.getJobDetail()
				.getJobDataMap().get(SCHEDULING_SERVICE);
		IScheduledJob job = (IScheduledJob) arg0.getJobDetail().getJobDataMap()
				.get(SCHEDULED_JOB);
		try {
			job.execute(service);
		} catch (Throwable e) {
			log.error("Job执行 失败", job.toString(), e);
		}
	}
}
