package com.easeye.quartz.quartzmonitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.conf.SystemConfigFile;
import com.easeye.quartz.quartzmonitor.object.QuartzClient;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.object.Scheduler;
import com.easeye.quartz.quartzmonitor.util.JMXUtil;
import com.easeye.quartz.quartzmonitor.util.PropertiesUtil;

/**
 * 处理应用与Quartz的连接（使用JMX）
 */
public class QuartzConnectServiceImpl implements QuartzConnectService {

	static Logger log = Logger.getLogger(QuartzConnectServiceImpl.class);
	
	@Override
	public QuartzClient initClient(QuartzConfig config) throws Exception {
	    //准备jmx连接参数
		Map<String, String[]> env = new HashMap<String, String[]>();
		env.put(JMXConnector.CREDENTIALS, new String[] { config.getUserName(), config.getPassword() });
		JMXServiceURL jmxServiceURL = JMXUtil.createQuartzInstanceConnection(config);
		//建立连接
		JMXConnector connector = JMXConnectorFactory.connect(jmxServiceURL, env);
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		//获取scheduler Mbean
		String schedulerJmxObjectName = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "schedulerJmxObjectName");
		ObjectName mBName = new ObjectName(schedulerJmxObjectName);
		Set<ObjectName> names = connection.queryNames(mBName, null);
		QuartzClient client = new QuartzClient();
		client.setConfig(config);
		client.setMBeanServerConnection(connection);
		client.setJmxConnector(connector);

		List<Scheduler> schList = new ArrayList<Scheduler>();
		for (ObjectName objectName : names) {  // for each scheduler.
			QuartzJMXAdapter jmxAdapter = QuartzJMXAdapterFactory.initQuartzJMXAdapter(objectName, connection);
			client.setJmxAdapter(jmxAdapter);

			Scheduler scheduler = jmxAdapter.getSchedulerByJmx(client, objectName);
			scheduler.setConfig(config);
			schList.add(scheduler);

			// attach listener
			// connection.addNotificationListener(objectName, listener, null, null);
			log.info("added listener " + objectName.getCanonicalName());
			// QuartzInstance.putListener(listener);
		}
		client.setSchedulerList(schList);
        QuartzClientContainer.addQuartzConfig(config);
        QuartzClientContainer.addQuartzClient(config.getConfigId(), client);
		return client;
	}
}