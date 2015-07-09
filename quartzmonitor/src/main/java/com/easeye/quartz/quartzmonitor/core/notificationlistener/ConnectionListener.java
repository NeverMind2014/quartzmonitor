package com.easeye.quartz.quartzmonitor.core.notificationlistener;

import javax.management.Notification;
import javax.management.NotificationListener;


/**
 * ConnectionListener(监听jmx连接状态)
 * @author zhenxing.li
 * @date 2015年7月9日 上午10:42:32
 * @version 
 * 
 */

public class ConnectionListener implements NotificationListener {
    /**
     * 将quartzConfig的连接状态改为
     * */
    @Override
    public void handleNotification(Notification notification, Object handback) {
        // TODO Auto-generated method stub

    }

}
