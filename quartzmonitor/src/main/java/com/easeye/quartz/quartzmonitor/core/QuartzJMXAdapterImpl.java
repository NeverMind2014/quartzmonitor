package com.easeye.quartz.quartzmonitor.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.notificationlistener.SchedulerNotificationListener;
import com.easeye.quartz.quartzmonitor.object.JMXInput;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.util.JMXUtil;
import com.easeye.quartz.quartzmonitor.util.QuartzUtil;
import com.easeye.quartz.quartzmonitor.util.Tools;

/**
 * http://www.quartz-scheduler.org/api/2.0.0/index.html?org/quartz/jobs/ee/jmx/JMXInvokerJob.html
 */
public class QuartzJMXAdapterImpl implements QuartzJMXAdapter {

	private static Logger log = Logger.getLogger(QuartzJMXAdapterImpl.class);

	@Override
	public String getVersion(QuartzClient client, ObjectName objectName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Job> getJobDetails(QuartzClient client, Scheduler scheduler)
			throws Exception {
		List<Job> jobs = null;
		JMXInput jmxInput = new JMXInput(client, null, "AllJobDetails", null,
				scheduler.getObjectName());
		TabularDataSupport tdata = (TabularDataSupport) JMXUtil.callJMXAttribute(jmxInput);
		if (tdata != null) {
			jobs = new ArrayList<Job>();
			for (Iterator<Object> it = tdata.values().iterator(); it.hasNext();) {
				Object object = (Object) it.next();
				if (!(object instanceof CompositeDataSupport)) {
					continue;
				}
				CompositeDataSupport compositeDataSupport = (CompositeDataSupport) object;
				Job job = new Job();
				job.setSchedulerName(scheduler.getName());
				job.setQuartzConfigId(scheduler.getClient().getConfig().getConfigId());
				job.setRemoteSchedulerInstanceId(scheduler.getRemoteInstanceId());
				job.setJobName((String) JMXUtil.convertToType(compositeDataSupport, "name"));
				log.info("job name:"+job.getJobName());
				job.setDescription((String) JMXUtil.convertToType(compositeDataSupport,"description"));
				job.setDurability(((Boolean) JMXUtil.convertToType(compositeDataSupport,"durability")).booleanValue());
				job.setShouldRecover(((Boolean) JMXUtil.convertToType(compositeDataSupport,"shouldRecover")).booleanValue());
				job.setGroup((String) JMXUtil.convertToType(compositeDataSupport, "group"));
				job.setJobClass((String) JMXUtil.convertToType(compositeDataSupport, "jobClass"));
				job.setScheduler(scheduler);
				job.setQuartzConfigId(scheduler.getClient().getConfig().getConfigId());
				// get Next Fire Time for job
				List<Trigger> triggers = this.getTriggersForJob(client, scheduler,
						job.getJobName(), job.getGroup());
				
				if(triggers == null || triggers.size() == 0){
					job.setState("NONE");
				}else{
					job.setState(getTriggerState(client,scheduler,triggers.get(0)));
				}
				
				log.info("job state:"+job.getState());
				try {
					if (triggers != null && triggers.size() > 0) {
						Date nextFireTime = QuartzUtil.getNextFireTimeForJob(triggers);
						job.setNextFireTime(nextFireTime);
						job.setNumTriggers(triggers.size());
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}

				log.debug("Loaded job: " + job);
				jobs.add(job);
			}
		}
		return jobs;
	}

	@Override
	public Scheduler getSchedulerById(QuartzClient client, String remoteInstanceId)
			throws Exception {
		
		List<Scheduler> list = client.getSchedulerList();
		if (list != null && list.size() > 0)
	      {
	         for (int i = 0; i < list.size(); i++)
	         {
	            Scheduler s = (Scheduler) list.get(i);
	            if (s.getRemoteInstanceId().equals(remoteInstanceId))
	            {
	               return s;
	            }
	         }
	      }
		return null;
	}

	@Override
	public void attachListener(QuartzClient client, ObjectName objectName,NotificationListener listener, NotificationFilter filter, Object handback) throws Exception {
        MBeanServerConnection connection = client.getMBeanServerConnection();
        connection.addNotificationListener(objectName, listener, filter, handback);
	}

    @Override
    public void removeListener(QuartzClient client, ObjectName objectName, NotificationListener listener,
            NotificationFilter filter, Object handback) throws Exception
    {
        MBeanServerConnection connection = client.getMBeanServerConnection();
        connection.removeNotificationListener(objectName, listener, filter, handback);
    }

	@Override
	public Scheduler getSchedulerByJmx(QuartzClient client, ObjectName objectName)
			throws Exception {
		  Scheduler scheduler = new Scheduler();
	      MBeanServerConnection connection = client.getMBeanServerConnection();
	      scheduler.setObjectName(objectName);
	      scheduler.setName((String) connection.getAttribute(objectName, "SchedulerName"));
	      scheduler.setInstanceId((String) connection.getAttribute(objectName, "SchedulerInstanceId"));
	      scheduler.setJobStoreClassName((String) connection.getAttribute(objectName, "JobStoreClassName"));
	      scheduler.setThreadPoolClassName((String) connection.getAttribute(objectName, "ThreadPoolClassName"));
	      scheduler.setThreadPoolSize((Integer) connection.getAttribute(objectName, "ThreadPoolSize"));
	      scheduler.setShutdown((Boolean) connection.getAttribute(objectName, "Shutdown"));
	      scheduler.setStarted((Boolean) connection.getAttribute(objectName, "Started"));
	      scheduler.setStandByMode((Boolean) connection.getAttribute(objectName, "StandbyMode"));
	      scheduler.setClient(client);
	      scheduler.setVersion(this.getVersion(client, objectName));
	      return scheduler;
	}

	@Override
	public void startJobNow(QuartzClient client, Scheduler scheduler,Job job) throws Exception {
		log.info("call start job.......");
		//HashMap map =new HashMap();
		//JMXInput jmxInput = new JMXInput(client, new String[]{String.class.getName(), String.class.getName(),"java.util.Map"}, "triggerJob", new Object[]{job.getJobName(), job.getGroup(),map}, scheduler.getObjectName());
		HashMap<String,Object> triggerMap = new HashMap<String,Object>();
		String triggerName = Tools.generateUUID();
		log.info(" start now trigger name  is " + triggerName);
		triggerMap.put("name",triggerName);
		triggerMap.put("group","now");
		triggerMap.put("description","立即执行");
		triggerMap.put("triggerClass", "org.quartz.impl.triggers.SimpleTriggerImpl");
		//triggerMap.put("calendarName", "");
		//triggerMap.put("startTime", new Date());
		//triggerMap.put("endTime", new Date());
		//triggerMap.put("repeatCount", Integer.valueOf(0));
		//triggerMap.put("repeatInterval",Long.valueOf(0));
		
		triggerMap.put("jobName", job.getJobName());
		triggerMap.put("jobGroup", job.getGroup());
		
		//Map<String,Object> jobMap = QuartzUtil.convertJob2Map(job);
//		JMXInput jmxInput = new JMXInput(client, new String[]{"java.util.Map","java.util.Map"}, "scheduleBasicJob", new Object[]{jobMap,triggerMap}, scheduler.getObjectName());
//	    JMXUtil.callJMXOperation(jmxInput);
	    
		JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String","java.util.Map"}, "scheduleJob", new Object[]{job.getJobName(),job.getGroup(),triggerMap}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
		
//	    JMXInput jmxInput1 = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "unscheduleJob", new Object[]{triggerName,"manager"}, scheduler.getObjectName());
//	    JMXUtil.callJMXOperation(jmxInput1);
	    
//	    JMXInput jmxInput1 = new JMXInput(client, new String[]{"java.lang.String","java.lang.String","java.lang.String","java.lang.String"}, "scheduleJob", new Object[]{job.getJobName(),job.getGroup(),"christmasDayJob","default"}, scheduler.getObjectName());
//	    JMXUtil.callJMXOperation(jmxInput1);
	}

	@Override
	public void deleteJob(QuartzClient client, Scheduler scheduler, Job job)
			throws Exception {
		JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "deleteJob", new Object[]{job.getJobName(),job.getGroup()}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void pauseJob(QuartzClient client, Scheduler scheduler, Job job)
			throws Exception {
			JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "pauseJob", new Object[]{job.getJobName(),job.getGroup()}, scheduler.getObjectName());
		    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void resumeJob(QuartzClient client, Scheduler scheduler, Job job)
			throws Exception {
		JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "resumeJob", new Object[]{job.getJobName(),job.getGroup()}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void addJob(QuartzClient instance, Scheduler scheduler,
			Map<String, Object> jobMap) throws Exception {
		JMXInput jmxInput = new JMXInput(instance, new String[]{"java.util.Map","boolean"}, "addJob", new Object[]{jobMap,false}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void updateJob(QuartzClient instance, Scheduler scheduler, 
			Map<String, Object> jobMap) throws Exception {
		JMXInput jmxInput = new JMXInput(instance, new String[]{"java.util.Map","boolean"}, "addJob", new Object[]{jobMap,true}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}
	
	@Override
	public void pauseTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception {
		JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "pauseTrigger", new Object[]{trigger.getName(), trigger.getGroup()}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void resumeTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger) throws Exception {
		JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "resumeTrigger", new Object[]{trigger.getName(), trigger.getGroup()}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	}
	


    @Override
    public void deleteTrigger(QuartzClient client, Scheduler scheduler, Trigger trigger)
            throws Exception {

        JMXInput jmxInput1 = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "unscheduleJob", new Object[]{trigger.getName(),trigger.getGroup()}, scheduler.getObjectName());
        JMXUtil.callJMXOperation(jmxInput1);
        
    }
    
    @Override
    public String getTriggerState(QuartzClient client, Scheduler scheduler, Trigger trigger)
            throws Exception {
        JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String"}, "getTriggerState", new Object[]{trigger.getName(),trigger.getGroup()}, scheduler.getObjectName());
        String state = (String)JMXUtil.callJMXOperation(jmxInput);
        return state;
    }

    @Override
    public void addTriggerForJob(QuartzClient client, Scheduler scheduler, Job job,
            Map<String,Object> triggerMap) throws Exception {
        //Map<String,Object> jobMap = QuartzUtil.convertJob2Map(job);
        
        /**
        jobMap.put("name", jobMap.get("name")+"Test");
        
        triggerMap.put("jobName", jobMap.get("name"));
        //这种方法会删除job原有的trigger
        JMXInput jmxInput = new JMXInput(client, new String[]{"java.util.Map","java.util.Map"}, "scheduleBasicJob", new Object[]{jobMap,triggerMap}, scheduler.getObjectName());
        JMXUtil.callJMXOperation(jmxInput);
        **/
        //必须指定trigger的class，也就是必须有存在的trigger
        
        //JMXInput jmxInput = new JMXInput(client, new String[]{"java.util.Map","java.util.Map"}, "scheduleJob", new Object[]{jobMap,triggerMap}, scheduler.getObjectName());
        JMXInput jmxInput = new JMXInput(client, new String[]{"java.lang.String","java.lang.String","java.util.Map"}, "scheduleJob", new Object[]{job.getJobName(),job.getGroup(),triggerMap}, scheduler.getObjectName());
        JMXUtil.callJMXOperation(jmxInput);
//      JMXInput jmxInput = new JMXInput(client, new String[]{"java.util.Map"}, "newTrigger", new Object[]{triggerMap}, scheduler.getObjectName());
//      JMXUtil.callJMXOperation(jmxInput);
    }

    /* 
     * 根据jobName groupName schedulerName 获取triggers
     */
    @Override
    public List<Trigger> getTriggersForJob(QuartzClient client, Scheduler scheduler,
            String jobName, String groupName) throws Exception {
        
          List<Trigger> triggers = null;

          JMXInput jmxInput = new JMXInput(client, new String[]{String.class.getName(), String.class.getName()}, "getTriggersOfJob", new Object[]{jobName, groupName}, scheduler.getObjectName());
          @SuppressWarnings("unchecked")
         List<CompositeDataSupport> list = (List<CompositeDataSupport>) JMXUtil.callJMXOperation(jmxInput);
          if (list != null && list.size() > 0)
          {
             log.info("-------"+jobName+" trigger size:"+list.size());
             triggers = new ArrayList<Trigger>();
             for (int i = 0; i < list.size(); i++)
             {
                CompositeDataSupport compositeDataSupport = (CompositeDataSupport) list.get(i);
                Trigger trigger = new Trigger();
                trigger.setCalendarName((String) JMXUtil.convertToType(compositeDataSupport, "calendarName"));
                log.info("-------"+jobName+" trigger's calendar name:"+trigger.getCalendarName());
                trigger.setDescription((String) JMXUtil.convertToType(compositeDataSupport, "description"));
                trigger.setEndTime((Date) JMXUtil.convertToType(compositeDataSupport, "endTime"));
                trigger.setFinalFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "finalFireTime"));
                trigger.setFireInstanceId((String) JMXUtil.convertToType(compositeDataSupport, "fireInstanceId"));
                trigger.setGroup((String) JMXUtil.convertToType(compositeDataSupport, "group"));
                trigger.setJobGroup((String) JMXUtil.convertToType(compositeDataSupport, "jobGroup"));
                trigger.setJobName((String) JMXUtil.convertToType(compositeDataSupport, "jobName"));
                log.info("-------"+jobName+" trigger's job name:"+trigger.getJobName());
                trigger.setMisfireInstruction(((Integer) JMXUtil.convertToType(compositeDataSupport, "misfireInstruction")).intValue());
                trigger.setName((String) JMXUtil.convertToType(compositeDataSupport, "name"));
                log.info("-------"+jobName+" trigger's  name:"+trigger.getName());
                trigger.setNextFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "nextFireTime"));
                log.info("-------"+jobName+" trigger's  nextFireTime:"+trigger.getNextFireTime());
                trigger.setPreviousFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "previousFireTime"));
                trigger.setPriority(((Integer) JMXUtil.convertToType(compositeDataSupport, "priority")).intValue());
                trigger.setStartTime((Date) JMXUtil.convertToType(compositeDataSupport, "startTime"));

                
                try 
                {
                   JMXInput stateJmxInput = new JMXInput(client, new String[]{String.class.getName(), String.class.getName()}, "getTriggerState", new Object[]{trigger.getName(), trigger.getGroup()}, scheduler.getObjectName());
                   String state = (String) JMXUtil.callJMXOperation(stateJmxInput);
                   trigger.setSTriggerState(state);
                }
                catch (Throwable tt)
                {
                   trigger.setSTriggerState(Trigger.STATE_GET_ERROR);
                }

                //删除group为"now"的trigger
                if(trigger.getGroup().equals("now")){
                    deleteTrigger(client, scheduler, trigger);
                }else{
                     triggers.add(trigger);
                }
             }
          }
          return triggers;
    }

    @Override
    public void schedulerStandby(QuartzClient client, Scheduler scheduler) throws Exception
    {
        JMXInput jmxInput = new JMXInput(client, new String[]{}, "standby", new Object[]{}, scheduler.getObjectName());
        JMXUtil.callJMXOperation(jmxInput);
        
    }

    @Override
    public void schedulerStart(QuartzClient client, Scheduler scheduler) throws Exception
    {
        JMXInput jmxInput = new JMXInput(client, new String[]{}, "start", new Object[]{}, scheduler.getObjectName());
        JMXUtil.callJMXOperation(jmxInput);
    }
}