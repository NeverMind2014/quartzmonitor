package com.easeye.quartz.quartzmonitor.core;

import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.service.QuartzConfigService;
import com.easeye.quartz.quartzmonitor.service.impl.QuartzConfigServiceImpl;

public class InitListener implements ServletContextListener {
	
	private static Logger logger = Logger.getLogger(InitListener.class);

	private QuartzConfigService schedulerService = new QuartzConfigServiceImpl();
	
	public void contextInitialized(ServletContextEvent event) {
		logger.info("load scheduler to scheduler container");
		
		try {
			List<QuartzConfig> list = schedulerService.getALLQuartzConfigs();
			for (QuartzConfig config : list) {
				try {
				    
//					QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
//					/*QuartzClient client = */quartzConnectService.initClient(config);
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
		
		/*String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "quartz-config";
		String path = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "configfilepath");
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			File[] fileList = file.listFiles();
			logger.info("find " + fileList.length + " configs of quartz config!");
			for (int i = 0; i < fileList.length; i++) {
				if (!fileList[i].isDirectory() && fileList[i].getName().startsWith("quartz-config-")) {
					try {
						QuartzConfig config = XstreamUtil.xml2Object(fileList[i].getAbsolutePath());
						QuartzInstanceContainer.addQuartzConfig(config);
						QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
						QuartzInstance quartzInstance = quartzConnectService.initInstance(config);
						QuartzInstanceContainer.addQuartzInstance(config.getUuid(), quartzInstance);
					} catch (FileNotFoundException e) {
						logger.error(e.getMessage(), e);
						e.printStackTrace();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						e.printStackTrace();
					}
				}
			}
		}*/
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}
}