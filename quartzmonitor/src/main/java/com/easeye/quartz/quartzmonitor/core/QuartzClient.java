package com.easeye.quartz.quartzmonitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easeye.quartz.quartzmonitor.conf.SystemConfigFile;
import com.easeye.quartz.quartzmonitor.core.notificationlistener.ConnectionListener;
import com.easeye.quartz.quartzmonitor.core.notificationlistener.MyNotificationListener;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.util.JMXUtil;
import com.easeye.quartz.quartzmonitor.util.PropertiesUtil;
import com.google.gson.Gson;

/**
 * @author zhenxing.li
 * @date 2015年7月7日 下午2:21:14
 * @version 
 */

public class QuartzClient{
    private Logger logger = LoggerFactory.getLogger(getClass());
	private QuartzConfig config;
	private MBeanServerConnection mBeanServerConnection;
	private QuartzJMXAdapter jmxAdapter;
	private List<Scheduler> schedulerList;
	
	public QuartzClient(QuartzConfig config){
	    this.config = config;
	}

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

//	public QuartzJMXAdapter getJmxAdapter() {
//		return jmxAdapter;
//	}

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
		addConnectionListener();
	}
	
	public boolean init() throws Exception{
	  //准备jmx连接参数
        Map<String, String[]> env = new HashMap<String, String[]>();
        env.put(JMXConnector.CREDENTIALS, new String[] { config.getUserName(), config.getPassword() });
        JMXServiceURL jmxServiceURL = JMXUtil.createQuartzInstanceConnection(config);
        //建立连接
        JMXConnector connector = JMXConnectorFactory.connect(jmxServiceURL, env);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        //获取scheduler Mbean
        String schedulerJmxObjectName = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "schedulerJmxObjectName");
        ObjectName mBName = new ObjectName(schedulerJmxObjectName);
        Set<ObjectName> names = connection.queryNames(mBName, null);
        this.setMBeanServerConnection(connection);
        this.setJmxConnector(connector);
        
        List<Scheduler> schList = new ArrayList<Scheduler>();
        for (ObjectName objectName : names) {  // for each scheduler.
            QuartzJMXAdapter jmxAdapter = QuartzJMXAdapterFactory.initQuartzJMXAdapter(objectName, connection);
            this.setJmxAdapter(jmxAdapter);

            Scheduler scheduler = jmxAdapter.getSchedulerByJmx(this, objectName);
            scheduler.setConfig(config);
            schList.add(scheduler);
            jmxAdapter.attachListener(this, objectName,new MyNotificationListener(),null,null);
            // attach listener
            // connection.addNotificationListener(objectName, listener, null, null);
//          log.info("added listener " + objectName.getCanonicalName());
            // QuartzInstance.putListener(listener);
        }
        this.setSchedulerList(schList);
        QuartzClientContainer.addQuartzConfig(config);
        QuartzClientContainer.addQuartzClient(config.getConfigId(), this);
	    return true;
	}
	
	public void destroy(){
	    
	}
    
	/**
     * addConnectionListener(给jmx连接增加一个监听器 来监控连接状态)
     * @return void 
     * @exception  
     * 
     */
    private void addConnectionListener(){
        Map<String,String> connectionInfo= new HashMap<String,String>();
        connectionInfo.put("host", config.getHost());
        connectionInfo.put("port", String.valueOf(config.getPort()));
        connectionInfo.put("configId", config.getConfigId());
        String jsonInfo = new Gson().toJson(connectionInfo);
        //添加一个监听器 监听连接状态
        jmxConnector.addConnectionNotificationListener(new ConnectionListener(), null, jsonInfo);
    }
	
	public QuartzConfig getConfig() {
        return config;
    }

    public void setConfig(QuartzConfig config) {
        this.config = config;
    }

    
    public String getVersion( ObjectName objectName) throws Exception {
        return this.jmxAdapter.getVersion(this, objectName);
    }

    
    public List<Job> getJobDetails(String schedulerName) throws Exception {
        // TODO Auto-generated method stub
        return this.jmxAdapter.getJobDetails(this, getSchedulerByName(schedulerName));
    }

    
    public Scheduler getSchedulerByInstanceId( String instanceId) throws Exception {
        // TODO Auto-generated method stub
        return this.jmxAdapter.getSchedulerById(this, instanceId);
    }

    
    public List<Trigger> getTriggersForJob(String schedulerName, String jobName, String groupName) throws Exception {
        // TODO Auto-generated method stub
        return this.jmxAdapter.getTriggersForJob(this, getSchedulerByName(schedulerName),jobName,groupName);
    }

    
    public void attachListener( ObjectName objectName, NotificationListener listener, NotificationFilter filter, Object handback) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.attachListener(this, objectName, listener, filter, handback);
    }

    
    public Scheduler getSchedulerByJmx( ObjectName objectName) throws Exception {
        // TODO Auto-generated method stub
        return this.jmxAdapter.getSchedulerByJmx(this, objectName);
    }

    
    public void startJobNow(String schedulerName, Job job) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.startJobNow(this, getSchedulerByName(schedulerName), job);
    }

    
    public void pauseJob(String schedulerName, Job job) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.pauseJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void pauseTrigger(String schedulerName, Trigger trigger) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.pauseTrigger(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public void deleteJob(String schedulerName, Job job) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.deleteJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void addJob(String schedulerName, Map<String, Object> jobMap) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.addJob(this, getSchedulerByName(schedulerName), jobMap);
    }

    
    public void updateJob(String schedulerName, Map<String, Object> jobMap)
            throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.updateJob(this, getSchedulerByName(schedulerName), jobMap);
    }

    
    public void deleteTrigger(String schedulerName, Trigger trigger) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.deleteTrigger(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public String getTriggerState(String schedulerName, Trigger trigger) throws Exception {
        // TODO Auto-generated method stub
        return this.jmxAdapter.getTriggerState(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public void addTriggerForJob(String schedulerName, Job job, Map<String, Object> triggerMap)
            throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.addTriggerForJob(this, getSchedulerByName(schedulerName), job, triggerMap);
    }

    
    public void resumeJob(String schedulerName, Job job) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.resumeJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void resumeTrigger(String schedulerName, Trigger trigger) throws Exception {
        // TODO Auto-generated method stub
        this.jmxAdapter.resumeTrigger(this, getSchedulerByName(schedulerName), trigger);
    }
    
    public List<Job> getAllJobs(){
        List<Job> jobList = new ArrayList<Job>();
        List<Scheduler> schedulers = getSchedulerList();
        if (schedulers != null && schedulers.size() > 0) {
            for (int i = 0; i < schedulers.size(); i++) {
                Scheduler scheduler = schedulers.get(i);
                List<Job> temp = null;
                try {
                    temp = getJobDetails(scheduler.getName());
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                if(CollectionUtils.isNotEmpty(temp)){
                    jobList.addAll(temp);
                }
            }
        }
        return jobList;
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