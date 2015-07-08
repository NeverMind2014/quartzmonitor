package com.easeye.quartz.quartzmonitor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import com.easeye.quartz.quartzmonitor.conf.SystemConfigFile;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class XstreamUtil {

	private  static Logger log = Logger.getLogger(XstreamUtil.class);
	
	private static XStream xs;
	
	static {
		xs = new XStream(new  StaxDriver());
		xs.alias("config", QuartzConfig.class);
	}
	
	public static void  object2XML(QuartzConfig config) throws IOException{
		
//		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"quartz-config/";
		String path = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "configfilepath") + SystemUtils.FILE_SEPARATOR;
		File file = new File(path + "quartz-config-"+config.getConfigId()+".xml");
		file.createNewFile();
		FileOutputStream fs = new FileOutputStream(file);
		log.info("create xml file : "+fs.toString());
		xs.toXML(config,fs);
	}
	public static QuartzConfig  xml2Object(String path) throws FileNotFoundException{
		
		log.info("load config from  xml file : "+ path);
		//FileOutputStream fs = new FileOutputStream(path);
		QuartzConfig config = new QuartzConfig();
		config = (QuartzConfig) xs.fromXML(new FileReader(path));
		return config;
	}
	
	public static QuartzConfig  removeXml(String uuid) throws FileNotFoundException{
		
//		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"quartz-config/";
		String path = PropertiesUtil.getPropertiesValue(SystemConfigFile.SYSCONFIG, "configfilepath") + SystemUtils.FILE_SEPARATOR;
		path = path +"quartz-config-" + uuid +".xml";
		File file = new File(path+uuid);
		log.info("remove xml file : "+ path);
		file.delete();
		
		QuartzConfig config = new QuartzConfig();
		config = (QuartzConfig) xs.fromXML(new FileReader(path));
		return config;
	}
}