package com.easeye.quartz.quartzmonitor.core;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.easeye.quartz.quartzmonitor.object.QuartzClient;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;

public class QuartzClientContainer {

	private static Map<String, QuartzClient> quartzClientMap = new ConcurrentHashMap<String, QuartzClient>();
	private static Map<String, QuartzConfig> configMap = new ConcurrentHashMap<String, QuartzConfig>();

	public static void addQuartzClient(String id, QuartzClient client) {
		quartzClientMap.put(id, client);
	}

	public static Map<String, QuartzClient> getQuartzClientMap() {
		return Collections.unmodifiableMap(quartzClientMap);
	}
	public static void removeQuartzClient(String clientId){
		quartzClientMap.remove(clientId);
	}
	
	public static QuartzClient getQuartzClient(String clientId){
	    return quartzClientMap.get(clientId);
	}
	public static void addQuartzConfig(QuartzConfig config) {
		
		configMap.put(config.getConfigId(),config);
	}
	
	public static Map<String, QuartzConfig> getConfigMap() {
		return Collections.unmodifiableMap(configMap);
	}
	public static QuartzConfig getQuartzConfig(String config){
		return configMap.get(config);
	}
	public static void removeQuartzConfig(String config){
		 configMap.remove(config);
	}
}