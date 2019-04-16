package com.stereo.study.schedule;

public interface IScheduledJob {

	public void execute(ISchedulingService service) throws  CloneNotSupportedException;

}
