package com.easeye.quartz.quartzmonitor.object;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.apache.commons.collections.CollectionUtils;

import com.easeye.quartz.quartzmonitor.core.QuartzClient;
import com.easeye.quartz.quartzmonitor.core.notificationlistener.SchedulerNotificationListener;

public class Scheduler {

	private String name;
	private ObjectName objectName;

	private String remoteInstanceId;//该属性来源于jmx远程数据
	private boolean started;
	private boolean shutdown;
	private boolean standByMode;
	private String version;
	private String jobStoreClassName;
	private String threadPoolClassName;
	private int threadPoolSize;
	private QuartzClient client;
	private List<SchedulerNotificationListener> listeners = new ArrayList<SchedulerNotificationListener>();
	
    public QuartzClient getClient() {
        return client;
    }

    public void setClient(QuartzClient client) {
        this.client = client;
    }

    public void setRemoteInstanceId(String remoteInstanceId) {
        this.remoteInstanceId = remoteInstanceId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemoteInstanceId() {
		return remoteInstanceId;
	}

	public void setInstanceId(String remoteInstanceId) {
		this.remoteInstanceId = remoteInstanceId;
	}

	public boolean isStarted() {
		return started;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public boolean isStandByMode() {
		return standByMode;
	}

	public void setStandByMode(boolean standByMode) {
		this.standByMode = standByMode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getJobStoreClassName() {
		return jobStoreClassName;
	}

	public void setJobStoreClassName(String jobStoreClassName) {
		this.jobStoreClassName = jobStoreClassName;
	}

	public String getThreadPoolClassName() {
		return threadPoolClassName;
	}

	public void setThreadPoolClassName(String threadPoolClassName) {
		this.threadPoolClassName = threadPoolClassName;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

    public List<SchedulerNotificationListener> getListeners()
    {
        return listeners;
    }

    public void setListeners(List<SchedulerNotificationListener> listeners)
    {
        this.listeners = listeners;
    }
    
    public void close(){
        //remove SchedulerNotificationListener if any
        if(CollectionUtils.isNotEmpty(listeners)){
            for (SchedulerNotificationListener listener : listeners)
            {
                try
                {
                    this.client.removeListener(this.objectName, listener, null, null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}