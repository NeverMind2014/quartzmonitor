package com.easeye.quartz.quartzmonitor.core.notificationlistener;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyNotificationListener implements NotificationListener {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleNotification(Notification notification, Object handback) {
        logger.info("notification msg: " + notification.getMessage());
        logger.info("notification type: " + notification.getType());
        logger.info("sequence num: "+String.valueOf(notification.getSequenceNumber()));
        logger.info("source: "+String.valueOf(notification.getSource()));
        logger.info("timeStemp: "+String.valueOf(notification.getTimeStamp()));
        logger.info("userData:"+String.valueOf(notification.getUserData()));
        if(null != handback){
            logger.info(String.valueOf(handback));
        }
    }

}
