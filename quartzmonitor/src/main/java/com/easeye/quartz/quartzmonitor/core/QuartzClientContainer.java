package com.easeye.quartz.quartzmonitor.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.service.QuartzConfigService;
import com.easeye.quartz.quartzmonitor.service.impl.QuartzConfigServiceImpl;

public class QuartzClientContainer {
    
    private static Logger logger = LoggerFactory.getLogger(QuartzClientContainer.class);

    private static QuartzConfigService schedulerService = new QuartzConfigServiceImpl();

	private static Map<String, QuartzClient> quartzClientMap = new ConcurrentHashMap<String, QuartzClient>();
	private static Map<String, QuartzConfig> configMap = new ConcurrentHashMap<String, QuartzConfig>();
	private static final ExecutorService EXECUTOR  = Executors.newSingleThreadExecutor();
	private static boolean isClosed = false;
	
	static{
	    runBackGround();
	}
	
	public static void close(){
	    isClosed = true;
	    EXECUTOR.shutdownNow();
	    closeClient();
	}
	
	public static void init(){
        logger.info("load scheduler to scheduler container");
        try {
            List<QuartzConfig> list = schedulerService.getALLQuartzConfigs();
            for (QuartzConfig config : list) {
                QuartzClientContainer.addQuartzConfig(config);
                try {
                    new QuartzClient(config).init();
                } catch (FileNotFoundException e) {
                    logger.error(e.getMessage(), e);
                    e.printStackTrace();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    
	    
	}

    private static void closeClient(){
        Set<Entry<String, QuartzClient>> entrySet = quartzClientMap.entrySet();
        for (Entry<String, QuartzClient> entry : entrySet) {
            QuartzClient client = entry.getValue();
            try {
                client.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        
    }
	
	public static void addQuartzClient(String id, QuartzClient client) {
		quartzClientMap.put(id, client);
	}

	public static Map<String, QuartzClient> getQuartzClientMap() {
		return Collections.unmodifiableMap(quartzClientMap);
	}
	public static void removeQuartzClient(String configId){
		quartzClientMap.remove(configId);
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
	
	public static void runBackGround(){
	    EXECUTOR.execute(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*60*2);
                }
                catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                while (!isClosed) {
                    try {
                    logger.info("=================定时检查断线连接开始=====================");
                    Set<Entry<String, QuartzConfig>> entrySet = configMap.entrySet();
                    for (Entry<String, QuartzConfig> entry : entrySet) {
                        QuartzConfig config = entry.getValue();
                        if (!config.isConnected()) {
                            logger.info("=================检查到断线连接-:"+config.getHost()+":"+config.getPort()+"=====================");
                            QuartzClient client = new QuartzClient(config);
                            try {
                                client.init();
                            }
                            catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        if(!config.isConnected()){
                            logger.info("================= 重连 "+config.getHost()+":"+config.getPort()+" 失败 =====================");
                        }
                    }
                    logger.info("=================定时检查断线连接结束=====================");
                        Thread.sleep(1000*60);
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
	    });
	    EXECUTOR.shutdown();
	}
}