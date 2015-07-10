package com.easeye.quartz.quartzmonitor.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class InitListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent event) {
	    QuartzClientContainer.init();
	}

	/* 
     *此方法没法再eclispe中调试
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    QuartzClientContainer.close();
//	    PrintWriter pw = null;
//	    try
//        {
//            File f = new File("c:/abc.txt");
//            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)));
//            pw.write("test............");
//        }
//        catch (FileNotFoundException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }finally{
//            if(null != pw){
//                pw.flush();
//                pw.close();
//            }
//        }
	}
}