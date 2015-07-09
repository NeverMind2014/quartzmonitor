package com.easeye.quartz.quartzmonitor.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.QuartzClient;
import com.easeye.quartz.quartzmonitor.core.QuartzClientContainer;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.Result;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.object.TriggerInput;
import com.easeye.quartz.quartzmonitor.util.JsonUtil;
import com.easeye.quartz.quartzmonitor.util.Tools;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class TriggerAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private  static Logger log = Logger.getLogger(TriggerAction.class);
	private String uuid;  //trigger的uuid
	private String jobId;
	private String triggeruuid;  //quartzInstanceId的uuid
	private String schedulerConfigId;//根据configId 可以拿到QuartzClient对象
	private String schedulerName;//根据QuartzClient对象的getSchedulerByName方法可以获取到scheduler对象
	private String jobName;//根据scheduler对象和jobName&groupName可以拿到远端的job对象
	private String jobGroupName;
	private String triggerName;
	private String triggerGroupName;
	private  List<Trigger> triggerList = new ArrayList<Trigger>();
	private Job job;
	private Trigger trigger;

	private TriggerInput triggerInput;
	
//	private TriggerService triggerService = new TriggerServiceImpl();
	
	private void deleteTrigger(Trigger trigger, Job job) throws Exception {
        QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
        client.deleteTrigger(job.getSchedulerName(), trigger);
	}
	
	private void addTrigger(Map<String, Object> triggerMap, Job job) throws Exception {
        QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
        client.addTriggerForJob(job.getSchedulerName(), job,triggerMap);
	}
	
	public String list() throws Exception {

		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		//根据schedulerObjectName jobName jobGroupName查询triggers
		List<Trigger> temp = client.getTriggersForJob(job.getSchedulerName(),job.getJobName(), job.getGroup());
		if(temp == null || temp.size() == 0){
	 		return "list";
		}
		triggerList.addAll(temp);
		log.info("job[" + jobName + "]'s trigger size:" + triggerList.size());
		return "list";
	}

	public String addShow() throws Exception {
		return "list";
	}

	public String add() throws Exception {
		HashMap<String, Object> triggerMap = new HashMap<String, Object>();
		triggerMap.put("name", triggerInput.getName());
		triggerMap.put("group",triggerInput.getGroup());
		triggerMap.put("description", triggerInput.getDescription());
		triggerMap.put("cronExpression", triggerInput.getCron());
		triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
		triggerMap.put("jobName", jobName);
		triggerMap.put("jobGroup", jobGroupName);
		Job job = new Job();
		job.setJobName(jobName);
		job.setGroup(jobGroupName);
		job.setSchedulerName(schedulerName);
		job.setQuartzConfigId(schedulerConfigId);
		addTrigger(triggerMap, job);
		
		log.info("add trigger for job:"+job.getJobName());
		
		Result result = new Result();
		result.setMessage("添加成功");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		
		
		return null;
	}

	public String delete() throws Exception {
		//根据schedulerObjectName triggerName triggerGroupName 删除trigger;
		deleteTrigger(trigger, job);
		
		log.info("delete job["+trigger.getJobName()+"]'s trigger!");
		Result result = new Result();
		result.setMessage("删除成功");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String pause() throws Exception {
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		client.pauseTrigger(job.getSchedulerName(), trigger);
		Result result = new Result();
		result.setMessage("trigger已暂停");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String resume() throws Exception {
        QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
//        client.getJmxAdapter().pauseTrigger(client, client.getSchedulerByName(job.getSchedulerName()), trigger);
		client.resumeTrigger(job.getSchedulerName(), trigger);
		
		Result result = new Result();
		result.setMessage("trigger已回复");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String edit() throws Exception {
		triggerInput = new TriggerInput();
		triggerInput.setName(trigger.getName());
		triggerInput.setGroup(trigger.getGroup());
		triggerInput.setCron(trigger.getCronExpression());
		triggerInput.setDescription(trigger.getDescription());
		return "edit";
	}
	
	/**
	 * update(更新 先把旧的删除 在添加一个新的 缓存中用同一个uuid)
	 *
	 * @return
	 * @throws Exception
	 * @return String 
	 * @exception  
	 * 
	 */
	public String update() throws Exception {
	    //根据schedulerObjectName triggerName triggerGroupName先删除原来的trigger
		deleteTrigger(trigger, job);
		//根据schedulerObjectName jobName jobGroupName添加新的trigger
		addTrigger(getTriggerMap(job), job);
		
		Result result = new Result();
		result.setMessage("修改成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	private HashMap<String, Object> getTriggerMap(Job job){
	    HashMap<String, Object> triggerMap = new HashMap<String, Object>();
        triggerMap.put("name", triggerInput.getName());
        triggerMap.put("group",triggerInput.getGroup());
        triggerMap.put("description", triggerInput.getDescription());
        triggerMap.put("cronExpression", triggerInput.getCron());
        triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
        triggerMap.put("jobName", job.getJobName());
        triggerMap.put("jobGroup", job.getGroup());
        return triggerMap;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getJobId() {
		return jobId;
	}


	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public String getTriggeruuid() {
		return triggeruuid;
	}

	public void setTriggeruuid(String triggeruuid) {
		this.triggeruuid = triggeruuid;
	}

	public List<Trigger> getTriggerList() {
		return triggerList;
	}


	public void setTriggerList(List<Trigger> triggerList) {
		this.triggerList = triggerList;
	}


	public TriggerInput getTriggerInput() {
		return triggerInput;
	}

	public void setTriggerInput(TriggerInput triggerInput) {
		this.triggerInput = triggerInput;
	}

    public String getSchedulerConfigId() {
        return schedulerConfigId;
    }

    public void setSchedulerConfigId(String schedulerConfigId) {
        this.schedulerConfigId = schedulerConfigId;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }
    
}