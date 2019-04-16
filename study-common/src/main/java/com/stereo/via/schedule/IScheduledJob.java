package com.stereo.via.schedule;

public interface IScheduledJob {

	public void execute(ISchedulingService service) throws  CloneNotSupportedException;

}
