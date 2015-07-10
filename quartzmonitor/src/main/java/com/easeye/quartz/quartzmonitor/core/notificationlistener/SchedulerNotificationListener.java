package com.easeye.quartz.quartzmonitor.core.notificationlistener;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;

import org.quartz.core.jmx.QuartzSchedulerMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


/**
 * SchedulerNotificationListener(监听远端scheduler事件) 
 * 可能的事件类型见{@link org.quartz.core.jmx.QuartzSchedulerMBean}
 * @author zhenxing.li
 * @date 2015年7月9日 下午2:21:05
 * @version 
 */

public class SchedulerNotificationListener implements NotificationListener {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleNotification(Notification notification, Object handback) {
        String notifyType = notification.getType();
        logger.info("************************"+notifyType+"***********************");
//        if(QuartzSchedulerMBean.JOB_ADDED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_DELETED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_EXECUTION_VETOED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_SCHEDULED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_TO_BE_EXECUTED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_UNSCHEDULED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOB_WAS_EXECUTED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOBS_PAUSED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.JOBS_RESUMED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.TRIGGER_FINALIZED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.TRIGGERS_PAUSED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.TRIGGERS_RESUMED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SCHEDULER_ERROR.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SCHEDULER_PAUSED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SCHEDULER_SHUTDOWN.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SCHEDULER_STARTED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SCHEDULING_DATA_CLEARED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SAMPLED_STATISTICS_ENABLED.equals(notifyType)){
//            
//        }else if(QuartzSchedulerMBean.SAMPLED_STATISTICS_RESET.equals(notifyType)){
//            
//        }else{
//            logger.info("未知的事件类型");
//        }
        logger.info("===================== notification msg: " + notification.getMessage());
        logger.info("===================== notification type: " + notification.getType());
        logger.info("===================== sequence num: "+String.valueOf(notification.getSequenceNumber()));
        logger.info("===================== source: "+String.valueOf(notification.getSource()));
        logger.info("===================== timeStemp: "+String.valueOf(notification.getTimeStamp()));
        logger.info("===================== userData:"+String.valueOf(notification.getUserData()));
        if(null != handback){
            logger.info("===================== "+String.valueOf(handback));
            @SuppressWarnings("unchecked")
            Map<String,String> connectInfo = new Gson().fromJson(String.valueOf(handback), Map.class);
            Set<Entry<String,String>> entrySet = connectInfo.entrySet();
            for (Entry<String, String> entry : entrySet) {
                logger.info("===================== "+entry.getKey()+": "+entry.getValue());
            }
        }
    }

}
