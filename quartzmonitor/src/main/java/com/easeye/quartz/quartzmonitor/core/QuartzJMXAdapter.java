package com.easeye.quartz.quartzmonitor.core;

import java.util.List;
import java.util.Map;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;

/**
 * 从JMX获取Quartz信息
 */
public interface QuartzJMXAdapter {
	public String getVersion(QuartzClient client, ObjectName objectName) throws Exception;

	public List<Job> getJobDetails(QuartzClient client, Scheduler scheduler) throws Exception;

	public Scheduler getSchedulerById(QuartzClient client, String scheduleID) throws Exception;

	public List<Trigger> getTriggersForJob(QuartzClient client, Scheduler scheduler, String jobName, String groupName) throws Exception;

	public void attachListener(QuartzClient client, ObjectName objectName, NotificationListener listener, NotificationFilter filter, Object handback) throws Exception;

	public Scheduler getSchedulerByJmx(QuartzClient client, ObjectName objectName) throws Exception;

	public void startJobNow(QuartzClient client, Scheduler scheduler, Job job) throws Exception;

	public void pauseJob(QuartzClient client, Scheduler scheduler, Job job) throws Exception;

	public void pauseTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception;
	
	public void deleteJob(QuartzClient client, Scheduler scheduler, Job job) throws Exception;

	public void addJob(QuartzClient instance, Scheduler schedulerByName, Map<String, Object> jobMap) throws Exception;
	
	public void updateJob(QuartzClient instance, Scheduler schedulerByName, Map<String, Object> jobMap) throws Exception;

	public void deleteTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception;

	public String getTriggerState(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception;

	public void addTriggerForJob(QuartzClient client, Scheduler scheduler, Job job, Map<String, Object> triggerMap) throws Exception;

	public void resumeJob(QuartzClient client, Scheduler scheduler, Job job) throws Exception;
	
	public void resumeTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception;
	
	public void schedulerStandby(QuartzClient client, Scheduler scheduler) throws Exception;

	public void schedulerStart(QuartzClient client, Scheduler scheduler) throws Exception;
}