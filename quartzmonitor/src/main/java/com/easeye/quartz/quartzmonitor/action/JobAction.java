package com.easeye.quartz.quartzmonitor.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.JobContainer;
import com.easeye.quartz.quartzmonitor.core.QuartzClientContainer;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.QuartzClient;
import com.easeye.quartz.quartzmonitor.object.Result;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.service.JobService;
import com.easeye.quartz.quartzmonitor.service.TriggerService;
import com.easeye.quartz.quartzmonitor.service.impl.JobServiceImpl;
import com.easeye.quartz.quartzmonitor.service.impl.TriggerServiceImpl;
import com.easeye.quartz.quartzmonitor.util.JsonUtil;
import com.easeye.quartz.quartzmonitor.util.Tools;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class JobAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(JobAction.class);
	
	private String uuid;  //quartzInstanceId
	private String jobuuid;  //job的uuid
	private List<Job> jobList = new ArrayList<Job>();
	private Map<String, Job> jobMap;
	private Job job;
	private Set<String> jobSet = new HashSet<String>();
	private Map<String, String> schedulerMap = new HashMap<String, String>();
	private List<String> jobDataMapKey = new ArrayList<String>();
	private List<String> jobDataMapValue = new ArrayList<String>();
    
    private List<Scheduler> schedulerList;
	
	private Integer pageNum = 1;// 当前页数
	private Integer numPerPage = 20;// 每页的数量
	private Integer pageCount;// 总页数
	private Integer size;

	private JobService jobService = new JobServiceImpl();
	private TriggerService triggerService = new TriggerServiceImpl();
	
	private void addTriggerRemote(Trigger trigger, Job nativeJob) throws Exception {
		HashMap<String, Object> triggerMap = new HashMap<String, Object>();
		triggerMap.put("name", trigger.getName());
		triggerMap.put("group",trigger.getGroup());
		triggerMap.put("description", trigger.getDescription());
		triggerMap.put("cronExpression", trigger.getCronExpression());
		triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
		triggerMap.put("jobName", trigger.getJobName());
		triggerMap.put("jobGroup", trigger.getGroup());
		
		QuartzClient client = Tools.getQuartzClient(nativeJob.getQuartzConfigId());
		client.getJmxAdapter().addTriggerForJob(client, client.getSchedulerByName(nativeJob.getSchedulerName()), nativeJob,triggerMap);
	}
	
	/**
	 * 在服务端添加一个本机job
	 * @param nativeJob
	 * @throws Exception
	 */
	private void addJobRemote(Job nativeJob) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", nativeJob.getJobName());
		map.put("group", nativeJob.getGroup());
		map.put("description", nativeJob.getDescription());
		map.put("jobClass", nativeJob.getJobClass());
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		
		QuartzClient client = Tools.getQuartzClient(nativeJob.getQuartzConfigId());
		client.getJmxAdapter().addJob(client, client.getSchedulerByName(nativeJob.getSchedulerName()), map);
		
//		List<Trigger> triggers = triggerService.getALLTriggers(nativeJob.getUuid());
//		for (Trigger trigger : triggers) {
//			trigger.setJobName(nativeJob.getJobName());
//			addTriggerRemote(trigger, nativeJob);
//		}
	}
	
	/**
	 * 把本地job同步到服务端
	 * @throws Exception
	 */
	private void syncJobs() throws Exception {
		Map<String, QuartzClient> quartzInstanceMap = QuartzClientContainer.getQuartzClientMap();
		for (Map.Entry<String, QuartzClient> entry : quartzInstanceMap.entrySet()) {
			QuartzClient client = entry.getValue();
			List<Scheduler> schedulers = client.getSchedulerList();
			log.info(" schedulers list size:" + schedulers.size());
			if (schedulers != null && schedulers.size() > 0) {
				for (int i = 0; i < schedulers.size(); i++) {
					Scheduler scheduler = schedulers.get(i);
					//同步
					List<Job> nativeJobs = jobService.getALLJobs(scheduler.getConfig().getConfigId());
					List<Job> remoteJobs = client.getJmxAdapter().getJobDetails(client, scheduler);
					for (Job nativeJob : nativeJobs) {
						boolean nativeJobExistInRemote = false;
						for (Job remoteJob :remoteJobs) {
							if (remoteJob.getJobName().equals(nativeJob.getJobName()) && remoteJob.getGroup().equals(nativeJob.getGroup())) {
								nativeJobExistInRemote = true;
								break;
							}
						}
						if (!nativeJobExistInRemote) {
							nativeJob.setSchedulerName(scheduler.getName());
							addJobRemote(nativeJob);
						}
					}
				}
			}
		}
	}
	
	public String list() throws Exception {
		Map<String, QuartzClient> quartzInstanceMap = QuartzClientContainer.getQuartzClientMap();
		for (Map.Entry<String, QuartzClient> entry : quartzInstanceMap.entrySet()) {
			QuartzClient client = entry.getValue();
			List<Scheduler> schedulers = client.getSchedulerList();
			log.info(" schedulers list size:" + schedulers.size());
			if (schedulers != null && schedulers.size() > 0) {
				for (int i = 0; i < schedulers.size(); i++) {
					Scheduler scheduler = schedulers.get(i);
					List<Job> temp = null;
					try {
                        temp = client.getJmxAdapter().getJobDetails(client, scheduler);
                    }
                    catch (Exception e) {
                        log.info(e.getMessage(),e);
                    }
					if(CollectionUtils.isNotEmpty(temp)){
					    List<Job> localJob = jobService.getALLJobs(scheduler.getConfig().getConfigId());
					    for (Job job : temp) {
					        //此处的id策略需优化 一直随机的uuid 随着方法调用的增加 会导致缓存map放置过多job 而且不可重用 又不会释放
					        //
					        String id = Tools.generateUUID();
					        job.setUuid(id);
//					        scheduler.getHost()+scheduler.getPort()+scheduler.getObjectName()+job.getJobName();
					        JobContainer.addJob(job.getUuid(), job);
					        jobList.add(job);
					    }
					}
				}
			}
		}
		pageCount = Tools.getPageSize(jobList.size(), numPerPage);
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageNum > pageCount) {
			pageNum = pageCount;
		}
		log.info("job size:" + jobList.size());
		size = jobList.size();
		return "list";
	}

	public String start() throws Exception {

		QuartzClient client = Tools.getQuartzClient(uuid);
		
		Job job = JobContainer.getJobById(jobuuid);
		client.getJmxAdapter().startJobNow(client, client.getSchedulerByName(job.getSchedulerName()), job);

		Result result = new Result();
		result.setStatusCode("200");
		result.setMessage("执行成功");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String delete() throws Exception {

//		jobService.deleteJob(jobuuid);
		Job job = JobContainer.removeJobById(jobuuid);
		QuartzClient client = Tools.getQuartzClient(job.getQuartzConfigId());
//		JobContainer.removeJobById(jobuuid);
		log.info("delete a quartz job!");
		client.getJmxAdapter().deleteJob(client, client.getSchedulerByName(job.getSchedulerName()), job);
		Result result = new Result();
		result.setMessage("删除成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String pause() throws Exception {

		QuartzClient client = Tools.getQuartzClient(uuid);

		Job job = JobContainer.getJobById(jobuuid);
		log.info("pause a quartz job!");
		client.getJmxAdapter().pauseJob(client, client.getSchedulerByName(job.getSchedulerName()), job);
		Result result = new Result();
		result.setMessage("Job已暂停");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String resume() throws Exception {

		QuartzClient client = Tools.getQuartzClient(uuid);

		Job job = JobContainer.getJobById(jobuuid);
		log.info("resume a quartz job!");
		client.getJmxAdapter().resumeJob(client, client.getSchedulerByName(job.getSchedulerName()), job);

		Result result = new Result();
		result.setMessage("Job已恢复");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String show() throws Exception {

//		Map<String, QuartzInstance> quartzInstanceMap = QuartzInstanceContainer.getQuartzInstanceMap();
//		for (Map.Entry<String, QuartzInstance> entry : quartzInstanceMap.entrySet()) {
//			QuartzInstance client = entry.getValue();
//			List<Scheduler> schedulers = client.getSchedulerList();
//			for (Scheduler scheduler : schedulers) {
////				jobSet.add(scheduler.getName());
//				schedulerMap.put(client.getUuid(), scheduler.getName());
//			}
//		}
        schedulerList = new ArrayList<Scheduler>();
        Map<String,QuartzClient> clientMap = QuartzClientContainer.getQuartzClientMap();
        Collection<QuartzClient> clients = clientMap.values();
        for (QuartzClient client : clients) {
            schedulerList.addAll(client.getSchedulerList());
        }
		return "add";
	}

	public String add() throws Exception {

//		QuartzInstance client = Tools.getQuartzInstance();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", job.getJobName());
		map.put("group", job.getGroup());
		map.put("description", job.getDescription());
//		map.put("jobClass", JobContainer.getJobById(job.getJobClass()).getJobClass());
		map.put("jobClass", job.getJobClass());
		
		if(jobDataMapKey.size() > 0){
			Map<String, Object> parammap = new HashMap<String, Object>();
			for (int i=0; i<jobDataMapKey.size(); i++) {
				parammap.put(jobDataMapKey.get(i), jobDataMapValue.get(i));
			}
			
			job.setJobDataMap(parammap);
			map.put("jobDataMap", job.getJobDataMap());   //job需要的参数
		}
		
		// map.put("jobDetailClass", "org.quartz.Job");
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		
		String curuuid = job.getUuid();
		job.setUuid(Tools.generateUUID());
		job.setQuartzConfigId(curuuid);
		QuartzClient client = Tools.getQuartzClient(curuuid);
		client.getJmxAdapter().addJob(client, client.getSchedulerByName(job.getSchedulerName()), map);
		
//		jobService.addJob(job);
		
		log.info("add job successfully!");

		Result result = new Result();
		result.setMessage("添加成功");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	public String edit() throws Exception {

		Job myJob = JobContainer.getJobById(jobuuid);
		
		job = new Job();
		job.setUuid(myJob.getUuid());
		job.setJobName(myJob.getJobName());
		job.setGroup(myJob.getGroup());
		job.setJobClass(myJob.getJobClass());
		job.setDescription(myJob.getDescription());
		job.setSchedulerName(myJob.getSchedulerName());
		return "edit";
	}
	
	public String update() throws Exception {
		Job myJob = JobContainer.getJobById(jobuuid);
		
		myJob.setJobClass(job.getJobClass());
		myJob.setDescription(job.getDescription());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", myJob.getJobName());
		map.put("group", myJob.getGroup());
		map.put("description", myJob.getDescription());
		map.put("jobClass", myJob.getJobClass());
		
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		
		QuartzClient client = Tools.getQuartzClient(myJob.getQuartzConfigId());
		client.getJmxAdapter().updateJob(client, client.getSchedulerByName(myJob.getSchedulerName()), map);
		
//		jobService.updateJob(myJob);
		
		Result result = new Result();
		result.setMessage("修改成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
 	public List<Job> getJobList() {
		return jobList;
	}

	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getJobuuid() {
		return jobuuid;
	}

	public void setJobuuid(String jobuuid) {
		this.jobuuid = jobuuid;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(Integer numPerPage) {
		this.numPerPage = numPerPage;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Map<String, Job> getJobMap() {
		return jobMap;
	}

	public void setJobMap(Map<String, Job> jobMap) {
		this.jobMap = jobMap;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Set<String> getJobSet() {
		return jobSet;
	}

	public void setJobSet(Set<String> jobSet) {
		this.jobSet = jobSet;
	}

	public Map<String, String> getSchedulerMap() {
		return schedulerMap;
	}

	public void setSchedulerMap(Map<String, String> schedulerMap) {
		this.schedulerMap = schedulerMap;
	}

	public List<String> getJobDataMapKey() {
		return jobDataMapKey;
	}

	public void setJobDataMapKey(List<String> jobDataMapKey) {
		this.jobDataMapKey = jobDataMapKey;
	}

	public List<String> getJobDataMapValue() {
		return jobDataMapValue;
	}

	public void setJobDataMapValue(List<String> jobDataMapValue) {
		this.jobDataMapValue = jobDataMapValue;
	}

    public List<Scheduler> getSchedulerList() {
        return schedulerList;
    }

    public void setSchedulerList(List<Scheduler> schedulerList) {
        this.schedulerList = schedulerList;
    }

}