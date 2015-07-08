package com.easeye.quartz.quartzmonitor.object;

import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.easeye.quartz.quartzmonitor.core.QuartzJMXAdapter;

/**
 * @author zhenxing.li
 * @date 2015年7月7日 下午2:21:14
 * @version 
 */

public class QuartzClient {
	private QuartzConfig config;
	private MBeanServerConnection mBeanServerConnection;
	private QuartzJMXAdapter jmxAdapter;
	private List<Scheduler> schedulerList;

	/**
	 * needed for shutdown. *
	 */
	private JMXConnector jmxConnector;

	public MBeanServerConnection getMBeanServerConnection() {
		return mBeanServerConnection;
	}

	public void setMBeanServerConnection(MBeanServerConnection mBeanServerConnection) {
		this.mBeanServerConnection = mBeanServerConnection;
	}

	public QuartzJMXAdapter getJmxAdapter() {
		return jmxAdapter;
	}

	public void setJmxAdapter(QuartzJMXAdapter jmxAdapter) {
		this.jmxAdapter = jmxAdapter;
	}

	public List<Scheduler> getSchedulerList() {
		return schedulerList;
	}

	public Scheduler getSchedulerByName(String name) {
		if (schedulerList != null && schedulerList.size() > 0) {
			for (int i = 0; i < schedulerList.size(); i++) {
				Scheduler s = (Scheduler) schedulerList.get(i);
				if (s.getName().equals(name)) {
					return s;
				}
			}
		}
		return null;
	}

	public void setSchedulerList(List<Scheduler> schedulerList) {
		this.schedulerList = schedulerList;
	}

	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}
	
	public QuartzConfig getConfig() {
        return config;
    }

    public void setConfig(QuartzConfig config) {
        this.config = config;
    }

    @Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("QuartzInstance");
		sb.append("{mBeanServerConnection=").append(mBeanServerConnection);
		sb.append(", jmxAdapter=").append(jmxAdapter);
		sb.append(", schedulerList=").append(schedulerList);
		sb.append('}');
		return sb.toString();
	}
}