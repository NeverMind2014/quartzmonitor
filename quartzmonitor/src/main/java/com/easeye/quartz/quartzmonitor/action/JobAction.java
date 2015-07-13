package com.easeye.quartz.quartzmonitor.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.QuartzClient;
import com.easeye.quartz.quartzmonitor.core.QuartzClientContainer;
import com.easeye.quartz.quartzmonitor.object.Job;
import com.easeye.quartz.quartzmonitor.object.Result;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.object.Trigger;
import com.easeye.quartz.quartzmonitor.util.JsonUtil;
import com.easeye.quartz.quartzmonitor.util.Tools;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

//	private JobService jobService = new JobServiceImpl();
	
	/**
	 * 远程添加 trigger
	 * @param trigger
	 * @param job
	 * @throws Exception
	 */
	private void addTriggerRemote(Trigger trigger, Job job) throws Exception {
		HashMap<String, Object> triggerMap = new HashMap<String, Object>();
		triggerMap.put("name", trigger.getName());
		triggerMap.put("group",trigger.getGroup());
		triggerMap.put("description", trigger.getDescription());
		triggerMap.put("cronExpression", trigger.getCronExpression());
		triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
		triggerMap.put("jobName", trigger.getJobName());
		triggerMap.put("jobGroup", trigger.getGroup());
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		client.addTriggerForJob(job.getSchedulerName(), job,triggerMap);
	}
	
	/**
	 * 在服务端添加一个本机job
	 * @param nativeJob
	 * @throws Exception
	 */
	private void addJobRemote(Job job) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", job.getJobName());
		map.put("group", job.getGroup());
		map.put("description", job.getDescription());
		map.put("jobClass", job.getJobClass());
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		client.addJob(job.getSchedulerName(), map);
	}
	/**
	 * 查询所有job
	 * @return
	 * @throws Exception
	 */
	public String list() throws Exception {
		Map<String, QuartzClient> quartzClientMap = QuartzClientContainer.getQuartzClientMap();
		for (Map.Entry<String, QuartzClient> entry : quartzClientMap.entrySet()) {
		    QuartzClient client = entry.getValue();
            jobList.addAll(client.getAllJobs());
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

	/**
	 * 查询某一个job
	 * @return
	 * @throws Exception
	 */
	public void getOneJob() throws Exception{
		Job job_1 = null;
		Map<String, QuartzClient> quartzClientMap = QuartzClientContainer.getQuartzClientMap();
		for (Map.Entry<String, QuartzClient> entry : quartzClientMap.entrySet()) {
		    QuartzClient client = entry.getValue();
            jobList.addAll(client.getAllJobs());
		}
		for (Job job_temp : jobList) {
			if(job_temp.getQuartzConfigId().equals(job.getQuartzConfigId())&&job_temp.getJobName().equals(job.getJobName())&&job_temp.getGroup().equals(job.getGroup())){
				job_1 = job_temp;
				break;
			}
		}
		ExclusionStrategy excludeStrategy = new SetterExclusionStrategy(new String[]{"schedulerList"});
    	Gson gson = new GsonBuilder().setExclusionStrategies(excludeStrategy).create();
		JsonUtil.toJson(gson.toJson(job_1));
	}
	
	/**
	 * 启动job  从服务端
	 * @return
	 * @throws Exception
	 */
	public String start() throws Exception {
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		client.startJobNow(job.getSchedulerName(), job);
		Result result = new Result();
		result.setStatusCode("200");
		result.setMessage("执行成功");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	/**
	 * 删除job
	 * @return
	 * @throws Exception
	 */
	public String delete() throws Exception {
	    //propJob();
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		log.info("delete a quartz job!");
		client.deleteJob(job.getSchedulerName(), job);
		Result result = new Result();
		result.setMessage("删除成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	/**
	 * 暂停job
	 * @return
	 * @throws Exception
	 */
	public String pause() throws Exception {
//	    找到需要暂停的job的client
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		log.info("pause a quartz job!");
//		找到scheduler并暂停对应的job
		client.pauseJob(job.getSchedulerName(), job);
		Result result = new Result();
		result.setMessage("Job已暂停");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	/**
	 * 恢复job
	 * @return
	 * @throws Exception
	 */
	public String resume() throws Exception {
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		log.info("resume a quartz job!");
		client.resumeJob(job.getSchedulerName(), job);
		Result result = new Result();
		result.setMessage("Job已恢复");
		result.setCallbackType("");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}

	/**
	 * 添加一个job
	 * @return
	 * @throws Exception
	 */
	public String show() throws Exception {
        schedulerList = new ArrayList<Scheduler>();
        Map<String,QuartzClient> clientMap = QuartzClientContainer.getQuartzClientMap();
        Collection<QuartzClient> clients = clientMap.values();
        for (QuartzClient client : clients) {
            schedulerList.addAll(client.getSchedulerList());
        }
		return "add";
	}

	/**
	 * add(根据scheduler ObjectName增加job)
	 *
	 * @return
	 * @throws Exception
	 * @return String 
	 * @exception  
	 * 
	 */
	public String add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", job.getJobName());
		map.put("group", job.getGroup());
		map.put("description", job.getDescription());
		map.put("jobClass", job.getJobClass());
		
		if(jobDataMapKey.size() > 0){
			Map<String, Object> parammap = new HashMap<String, Object>();
			for (int i=0; i<jobDataMapKey.size(); i++) {
				parammap.put(jobDataMapKey.get(i), jobDataMapValue.get(i));
			}
			job.setJobDataMap(parammap);
			map.put("jobDataMap", job.getJobDataMap());   //job需要的参数
		}
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		try{
		//找到需要增加job的client
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		//找到需要添加job的scheduler 并添加job
		client.addJob(job.getSchedulerName(), map);
		log.info("add job successfully!");
		}catch (Exception e) {
			this.addActionError("添加失败,"+e.getMessage());
			 schedulerList = new ArrayList<Scheduler>();
		     Map<String,QuartzClient> clientMap = QuartzClientContainer.getQuartzClientMap();
		     Collection<QuartzClient> clients = clientMap.values();
		     for (QuartzClient client : clients) {
		         schedulerList.addAll(client.getSchedulerList());
		     }
			return "input";
		}
		this.addActionMessage("添加成功!");
		return "success"; 
	}

	/**
	 * 修改作业
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
	  //  propJob();
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		List<Job> jobs= client.getAllJobs();
		for (Job job_1 : jobs) {
			if(job_1.getJobName().equals(job.getJobName())&&job_1.getGroup().equals(job.getGroup())&&job_1.getSchedulerName().equals(job.getSchedulerName())){
				job = job_1;
			}
		}
		return "edit";
	}
	
//	private void propJob(){
//	    String[] props = jobuuid.split("@");
//        job = new Job();
//        job.setQuartzConfigId(props[0]);
//        job.setJobName(props[1]);
//        job.setGroup(props[2]);
//        job.setSchedulerName(props[3]);
//        job.setJobClass(props[4]);
//        if(props.length==6){
//            job.setDescription(props[5]);
//        }
//	}
	/**
	 * 更新作业
	 * @return
	 * @throws Exception
	 */
	public String update() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", job.getJobName());
		map.put("group", job.getGroup());
		map.put("description", job.getDescription());
		map.put("jobClass", job.getJobClass());
		map.put("durability", true);
		map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
		
		QuartzClient client = QuartzClientContainer.getQuartzClient(job.getQuartzConfigId());
		try {
			client.updateJob(job.getSchedulerName(), map);
		} catch (Exception e) {
			// TODO: handle exception
			this.addActionError("更新出错,"+e.getMessage());
			return "input";
		}
		this.addActionMessage("更新成功!");
		return "success";
	}
	
	
	/**
	 * 过滤帮助类
	 * 
	 * @author bamboo
	 * 
	 */
	private static class SetterExclusionStrategy implements ExclusionStrategy {
		private String[] fields;

		public SetterExclusionStrategy(String[] fields) {
			this.fields = fields;

		}

		@Override
		public boolean shouldSkipClass(Class<?> arg0) {
			return false;
		}

		/**
		 * 过滤字段的方法
		 */
		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			if (fields != null) {
				for (String name : fields) {
					if (f.getName().equals(name)) {
						/** true 代表此字段要过滤 */
						return true;
					}
				}
			}
			return false;
		}
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