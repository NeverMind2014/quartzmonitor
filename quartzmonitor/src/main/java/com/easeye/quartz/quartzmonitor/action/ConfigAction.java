package com.easeye.quartz.quartzmonitor.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.core.QuartzClientContainer;
import com.easeye.quartz.quartzmonitor.core.QuartzConnectService;
import com.easeye.quartz.quartzmonitor.core.QuartzConnectServiceImpl;
import com.easeye.quartz.quartzmonitor.object.QuartzClient;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.object.Result;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.service.QuartzConfigService;
import com.easeye.quartz.quartzmonitor.service.impl.QuartzConfigServiceImpl;
import com.easeye.quartz.quartzmonitor.util.JsonUtil;
import com.easeye.quartz.quartzmonitor.util.Tools;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class ConfigAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private  static Logger log = Logger.getLogger(ConfigAction.class);
	
	private String uuid;
	private String host;
	private int port;
	private String username;
	private String password;
	
	private QuartzConfigService configService = new QuartzConfigServiceImpl();
	
	private Map<String,QuartzConfig> quartzMap;
	
	private List<Scheduler> schedulerList;
	

	public String add() throws Exception {

		String id = Tools.generateUUID();
		QuartzConfig quartzConfig = new QuartzConfig(id, host, port, username, password);
		QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
		quartzConnectService.initClient(quartzConfig);
		//QuartzInstanceService.putQuartzInstance(quartzInstance);
		log.info("add a quartz info!");
		
	    configService.addQuartzConfig(quartzConfig);
		
//		XstreamUtil.object2XML(quartzConfig);
		
		Result result = new Result();
		result.setNavTabId("main");
		result.setMessage("添加成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String list() throws Exception {

		quartzMap = QuartzClientContainer.getConfigMap();
		
		log.info("get quartz map info.map size:"+quartzMap.size());
		
		return "list";
	}
    
    public String listScheduler() throws Exception {

        schedulerList = new ArrayList<Scheduler>();
        Map<String,QuartzClient> instanceMap = QuartzClientContainer.getQuartzClientMap();
        Collection<QuartzClient> instances = instanceMap.values();
        for (QuartzClient quartzInstance : instances) {
            schedulerList.addAll(quartzInstance.getSchedulerList());
        }
        log.info("get schedulerList size:"+schedulerList.size());
        
        return "listScheduler";
    }

	
	public String show() throws Exception {

		QuartzConfig quartzConfig = QuartzClientContainer.getQuartzConfig(uuid);
		log.info("get a quartz info! uuid:"+uuid);
		uuid = quartzConfig.getConfigId();
		host = quartzConfig.getHost();
		port = quartzConfig.getPort();
		username = quartzConfig.getUserName();
		password = quartzConfig.getPassword();
		return "show";
	}
	
	public String update() throws Exception {

		QuartzConfig quartzConfig = new QuartzConfig(uuid,host, port, username,password);
		QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
		QuartzClient client = quartzConnectService.initClient(quartzConfig);
		QuartzClientContainer.addQuartzConfig(quartzConfig);
		QuartzClientContainer.addQuartzClient(uuid, client);
		log.info("update a quartz info!");
		
	    configService.updateQuartzConfig(quartzConfig);
		
//		XstreamUtil.object2XML(quartzConfig);
		
		Result result = new Result();
		result.setMessage("修改成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	public String delete() throws Exception {

		QuartzClientContainer.removeQuartzConfig(uuid);
		QuartzClientContainer.removeQuartzClient(uuid);
		log.info("delete a quartz info!");
		
		configService.deleteQuartzConfig(uuid);
		
//		XstreamUtil.removeXml(uuid);
		
		Result result = new Result();
		result.setMessage("删除成功");
		JsonUtil.toJson(new Gson().toJson(result));
		return null;
	}
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public Map<String, QuartzConfig> getQuartzMap() {
		return quartzMap;
	}
	public void setQuartzMap(Map<String, QuartzConfig> quartzMap) {
		this.quartzMap = quartzMap;
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

    public List<Scheduler> getSchedulerList() {
        return schedulerList;
    }

    public void setSchedulerList(List<Scheduler> schedulerList) {
        this.schedulerList = schedulerList;
    }

}