package com.easeye.quartz.quartzmonitor.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ListenerNotFoundException;
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
import com.easeye.quartz.quartzmonitor.core.notificationlistener.SchedulerNotificationListener;
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
	private boolean isInitiated = false;
	private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
	
	public QuartzClient(QuartzConfig config){
	    this.config = config;
	}

	/**
	 * needed for shutdown. *
	 */
	private JMXConnector jmxConnector;

	public Scheduler getSchedulerByName(String name) {
		if (schedulerList != null && schedulerList.size() > 0) {
			for (int i = 0; i < schedulerList.size(); i++) {
				Scheduler s = schedulerList.get(i);
				if (s.getName().equals(name)) {
					return s;
				}
			}
		}
		return null;
	}
	
	public boolean init() throws Exception{
        try {
            //准备jmx连接参数
            Map<String, String[]> env = new HashMap<String, String[]>();
            env.put(JMXConnector.CREDENTIALS, new String[] { config.getUserName(), config.getPassword() });
            JMXServiceURL jmxServiceURL = JMXUtil.genJMXURL(config);
            //建立连接
            JMXConnector connector = JMXConnectorFactory.connect(jmxServiceURL, env);
            this.config.setConnected(true);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            QuartzJMXAdapter adapter = QuartzJMXAdapterFactory.initQuartzJMXAdapter(connection);
            
            this.jmxConnector = connector;
            //添加一个监听器 监听连接状态
            addConnectionListener();
            this.mBeanServerConnection = connection;
            this.jmxAdapter = adapter;
            
            //获取scheduler Mbean
            String schedulerJmxObjectName = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "schedulerJmxObjectName");
            ObjectName mBName = new ObjectName(schedulerJmxObjectName);
            Set<ObjectName> names = connection.queryNames(mBName, null);
            List<Scheduler> schList = new ArrayList<Scheduler>();
            for (ObjectName objectName : names) {  // for each scheduler.
                Scheduler scheduler = getSchedulerByJmx(objectName);
                scheduler.setClient(this);
                schList.add(scheduler);
//                根据需要增加对调度器的监听器
                Map<String,String> handback = new HashMap<String,String>();
                handback.put("schedulerName", scheduler.getName());
                handback.put("configId", scheduler.getClient().getConfig().getConfigId());
                handback.put("host", scheduler.getClient().getConfig().getHost());
                handback.put("port", scheduler.getClient().getConfig().getPort()+"");
                String jsonInfo = new Gson().toJson(handback);
                SchedulerNotificationListener listener = new SchedulerNotificationListener();
                jmxAdapter.attachListener(this, objectName,listener,null,jsonInfo);
                scheduler.getListeners().add(listener);
            }
            this.schedulerList = schList;
            //将config 和 client添加到缓存
            this.isInitiated = true;
            QuartzClientContainer.addQuartzClient(config.getConfigId(), this);
            logger.info("成功连接到远端JMX服务,服务信息："+jmxServiceURL);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
	    return this.isInitiated;
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
        connectionInfo.put(QuartzConfig.HOST, config.getHost());
        connectionInfo.put(QuartzConfig.PORT, String.valueOf(config.getPort()));
        connectionInfo.put(QuartzConfig.CONFIG_ID, config.getConfigId());
        String jsonInfo = new Gson().toJson(connectionInfo);
        //添加一个监听器 监听连接状态
        ConnectionListener listener = new ConnectionListener();
        connectionListeners.add(listener);
        jmxConnector.addConnectionNotificationListener(listener, null, jsonInfo);
    }

    public String getVersion( ObjectName objectName) throws Exception {
        return this.jmxAdapter.getVersion(this, objectName);
    }

    
    public List<Job> getJobDetails(String schedulerName) throws Exception {
        
        return this.jmxAdapter.getJobDetails(this, getSchedulerByName(schedulerName));
    }

    
    public Scheduler getSchedulerByInstanceId( String instanceId) throws Exception {
        
        return this.jmxAdapter.getSchedulerById(this, instanceId);
    }

    
    public List<Trigger> getTriggersForJob(String schedulerName, String jobName, String groupName) throws Exception {
        
        return this.jmxAdapter.getTriggersForJob(this, getSchedulerByName(schedulerName),jobName,groupName);
    }

    
    public void attachListener( ObjectName objectName, NotificationListener listener, NotificationFilter filter, Object handback) throws Exception {
        
        this.jmxAdapter.attachListener(this, objectName, listener, filter, handback);
    }

    
    public void removeListener( ObjectName objectName, NotificationListener listener, NotificationFilter filter, Object handback) throws Exception {
        
        this.jmxAdapter.removeListener(this, objectName, listener, filter, handback);
    }

    
    public Scheduler getSchedulerByJmx( ObjectName objectName) throws Exception {
        
        return this.jmxAdapter.getSchedulerByJmx(this, objectName);
    }

    
    public void startJobNow(String schedulerName, Job job) throws Exception {
        
        this.jmxAdapter.startJobNow(this, getSchedulerByName(schedulerName), job);
    }

    
    public void pauseJob(String schedulerName, Job job) throws Exception {
        
        this.jmxAdapter.pauseJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void pauseTrigger(String schedulerName, Trigger trigger) throws Exception {
        
        this.jmxAdapter.pauseTrigger(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public void deleteJob(String schedulerName, Job job) throws Exception {
        
        this.jmxAdapter.deleteJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void addJob(String schedulerName, Map<String, Object> jobMap) throws Exception {
        
        this.jmxAdapter.addJob(this, getSchedulerByName(schedulerName), jobMap);
    }

    
    public void updateJob(String schedulerName, Map<String, Object> jobMap)
            throws Exception {
        
        this.jmxAdapter.updateJob(this, getSchedulerByName(schedulerName), jobMap);
    }

    
    public void deleteTrigger(String schedulerName, Trigger trigger) throws Exception {
        
        this.jmxAdapter.deleteTrigger(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public String getTriggerState(String schedulerName, Trigger trigger) throws Exception {
        
        return this.jmxAdapter.getTriggerState(this, getSchedulerByName(schedulerName), trigger);
    }

    
    public void addTriggerForJob(String schedulerName, Job job, Map<String, Object> triggerMap)
            throws Exception {
        
        this.jmxAdapter.addTriggerForJob(this, getSchedulerByName(schedulerName), job, triggerMap);
    }

    
    public void resumeJob(String schedulerName, Job job) throws Exception {
        
        this.jmxAdapter.resumeJob(this, getSchedulerByName(schedulerName), job);
    }

    
    public void resumeTrigger(String schedulerName, Trigger trigger) throws Exception {
        
        this.jmxAdapter.resumeTrigger(this, getSchedulerByName(schedulerName), trigger);
    }
    
    
    public void schedulerStandby(Scheduler scheduler) throws Exception{
        this.jmxAdapter.schedulerStandby(this, scheduler);
    }

    public void schedulerStart(Scheduler scheduler) throws Exception{
        this.jmxAdapter.schedulerStart(this, scheduler);
    }
    
    /**/
    
    public boolean addScheduler(Scheduler scheduler){
        Scheduler old = getSchedulerByName(scheduler.getName());
        if(null == old){
            synchronized (schedulerList) {
                schedulerList.add(scheduler);
            }
            return true;
        }
        return false;
    }
    
    public boolean removeScheduler(Scheduler scheduler){
        scheduler = getSchedulerByName(scheduler.getName());
        int index = schedulerList.indexOf(scheduler);
        if(index != -1){
            synchronized (schedulerList) {
                schedulerList.remove(index);
            }
            return true;
        }
        return false;
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
    
    public MBeanServerConnection getMBeanServerConnection() {
        return mBeanServerConnection;
    }

    public QuartzJMXAdapter getJmxAdapter() {
        return jmxAdapter;
    }
    
    public boolean isInitiated() {
        return isInitiated;
    }

    public List<Scheduler> getSchedulerList() {
        return schedulerList;
    }

    public JMXConnector getJmxConnector() {
        return jmxConnector;
    }
    
    public QuartzConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("QuartzClient");
        sb.append("{mBeanServerConnection=").append(mBeanServerConnection);
        sb.append(", jmxAdapter=").append(jmxAdapter);
        sb.append(", schedulerList=").append(schedulerList);
        sb.append('}');
        return sb.toString();
    }
    
    public void close() throws IOException{
        //remove connectionListener if any
        if(CollectionUtils.isNotEmpty(connectionListeners)){
            for (ConnectionListener listener : connectionListeners)
            {
                try
                {
                    this.jmxConnector.removeConnectionNotificationListener(listener);
                }
                catch (ListenerNotFoundException e)
                {
                    logger.error(e.getMessage(),e);
                }
            }
        }
        //close scheduler
        if(CollectionUtils.isNotEmpty(schedulerList)){
            for (Scheduler sched : schedulerList)
            {
                sched.close();
            }
        }
        this.jmxConnector.close();
        QuartzClientContainer.removeQuartzConfig(this.getConfig().getConfigId());
        QuartzClientContainer.removeQuartzClient(this.getConfig().getConfigId());
    }
}