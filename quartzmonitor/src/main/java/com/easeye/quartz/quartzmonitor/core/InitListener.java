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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    QuartzClientContainer.close();
	}
}