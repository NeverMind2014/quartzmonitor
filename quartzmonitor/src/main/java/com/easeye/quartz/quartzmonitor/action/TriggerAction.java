package com.easeye.quartz.quartzmonitor.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.JobContainer;
import com.easeye.quartz.quartzmonitor.core.TriggerContainer;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.QuartzClient;
import com.easeye.quartz.quartzmonitor.object.Result;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.object.TriggerInput;
import com.easeye.quartz.quartzmonitor.service.TriggerService;
import com.easeye.quartz.quartzmonitor.service.impl.TriggerServiceImpl;
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
	private  List<Trigger> triggerList = new ArrayList<Trigger>();

	private TriggerInput triggerInput;
	
//	private TriggerService triggerService = new TriggerServiceImpl();
	
	private Trigger deleteTrigger(Trigger trigger, Job job) throws Exception {
        QuartzClient instance = Tools.getQuartzClient(job.getQuartzConfigId());
		instance.getJmxAdapter().deleteTrigger(instance, instance.getSchedulerByName(job.getSchedulerName()), trigger);
		return TriggerContainer.removeTriggerById(trigger.getUuid());
	}
	
	private void addTrigger(Map<String, Object> triggerMap, Job job, String oldUuid) throws Exception {
	    String uuid = StringUtils.isBlank(oldUuid)?Tools.generateUUID():oldUuid;
        QuartzClient instance = Tools.getQuartzClient(job.getQuartzConfigId());
		instance.getJmxAdapter().addTriggerForJob(instance, instance.getSchedulerByName(job.getSchedulerName()), job,triggerMap);
		Trigger trigger = new Trigger();
		trigger.setName(triggerMap.get("name").toString());
		trigger.setGroup(triggerMap.get("group").toString());
		trigger.setDescription(triggerMap.get("description").toString());
		trigger.setJobId(job.getUuid());
		trigger.setCronExpression(triggerMap.get("cronExpression").toString());
		trigger.setUuid(uuid);
		TriggerContainer.addTrigger(uuid, trigger);;
	}
	
	public String list() throws Exception {

		QuartzClient instance = Tools.getQuartzClient(triggeruuid);
		List<Scheduler> schedulers = instance.getSchedulerList();
		log.info(" schedulers list size:"+schedulers.size());

		Job job = JobContainer.getJobById(jobId);
		Scheduler scheduler = instance.getSchedulerByName(job.getSchedulerName());

		List<Trigger> temp = instance.getJmxAdapter().getTriggersForJob(instance, scheduler,job.getJobName(), job.getGroup());
		if(temp == null || temp.size() == 0){
			return "list";
		}
		
		for (Trigger trigger : temp) {
		    //id不应该使用随机的uuid
            String id = Tools.generateUUID();
			trigger.setUuid(id);
			trigger.setJobId(jobId);
			TriggerContainer.addTrigger(trigger.getUuid(), trigger);
			triggerList.add(trigger);
			System.out.println(trigger.getSTriggerState());
		}
		log.info("job[" + job.getJobName() + "]'s trigger size:" + triggerList.size());
		return "list";
	}

	public String addShow() throws Exception {
		return "list";
	}

	public String add() throws Exception {

//		QuartzInstance instance = Tools.getQuartzInstance(triggeruuid);
		
		HashMap<String, Object> triggerMap = new HashMap<String, Object>();
		triggerMap.put("name", triggerInput.getName());
		triggerMap.put("group",triggerInput.getGroup());
		triggerMap.put("description", triggerInput.getDescription());
		triggerMap.put("cronExpression", triggerInput.getCron());
		triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
		Job job = JobContainer.getJobById(jobId);
		triggerMap.put("jobName", job.getJobName());
		triggerMap.put("jobGroup", job.getGroup());
		
		
		addTrigger(triggerMap, job,null);
		
		log.info("add trigger for job:"+job.getJobName());
		
		Result result = new Result();
		result.setMessage("添加成功");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		
		
		return null;
	}

	public String delete() throws Exception {
		
//		QuartzInstance instance = Tools.getQuartzInstance(triggeruuid);
		
		Trigger trigger = TriggerContainer.getTriggerById(uuid);
		
		deleteTrigger(trigger, JobContainer.getJobById(trigger.getJobId()));
		
		log.info("delete job["+trigger.getJobName()+"]'s trigger!");
		Result result = new Result();
		result.setMessage("删除成功");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String pause() throws Exception {
		QuartzClient instance = Tools.getQuartzClient(triggeruuid);
		
		Trigger trigger = TriggerContainer.getTriggerById(uuid);
		Job job = JobContainer.getJobById(trigger.getJobId());
		instance.getJmxAdapter().pauseTrigger(instance, instance.getSchedulerByName(job.getSchedulerName()), trigger);
		
		Result result = new Result();
		result.setMessage("trigger已暂停");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String resume() throws Exception {
		QuartzClient instance = Tools.getQuartzClient(triggeruuid);
		
		Trigger trigger = TriggerContainer.getTriggerById(uuid);
		Job job = JobContainer.getJobById(trigger.getJobId());
		instance.getJmxAdapter().resumeTrigger(instance, instance.getSchedulerByName(job.getSchedulerName()), trigger);
		
		Result result = new Result();
		result.setMessage("trigger已回复");
		result.setNavTabId("triggerList");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String edit() throws Exception {
		triggerInput = new TriggerInput();
		Trigger trigger = TriggerContainer.getTriggerById(uuid);
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
		Trigger removed = TriggerContainer.removeTriggerById(uuid);
		
		Job job = JobContainer.getJobById(removed.getJobId());
		deleteTrigger(removed, job);
		
		addTrigger(getTriggerMap(job), job,removed.getUuid());
		
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

}