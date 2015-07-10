package com.easeye.quartz.quartzmonitor.core.notificationlistener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easeye.quartz.quartzmonitor.core.QuartzClientContainer;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.google.gson.Gson;


/**
 * ConnectionListener(监听jmx连接状态)
 * @author zhenxing.li
 * @date 2015年7月9日 上午10:42:32
 * @version 
 * 
 */

public class ConnectionListener implements NotificationListener {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 当发生连接事件时记录日志 并做相应处理
     * 如果事件为连接已断开 则将QuartzConfig.isConnected设置为false;
     * 关于通知可能的类型, 见{@link JMXConnectionNotification}文档.
     * */
    @Override
    public void handleNotification(Notification notification, Object handback) {
        String notifyType = notification.getType();
        logger.info("************************"+notifyType+"***********************");
        Map<String,String> connectInfo = null;
        if(null != handback){
            connectInfo = new Gson().fromJson(String.valueOf(handback), Map.class);
            Set<Entry<String,String>> entrySet = connectInfo.entrySet();
            for (Entry<String, String> entry : entrySet) {
                logger.info("===================== "+entry.getKey()+" : "+entry.getValue()+" =====================");
            }
        }
        if(JMXConnectionNotification.CLOSED.equals(notifyType)){
            if (null != connectInfo) {
                String configId = connectInfo.get(QuartzConfig.CONFIG_ID);
                if (StringUtils.isNotBlank(configId)) {
                    QuartzConfig config = QuartzClientContainer.getQuartzConfig(configId);
                    if (null != config) {
                        config.setConnected(false);
                    }
                    QuartzClientContainer.removeQuartzClient(config.getConfigId());
                }
            }
        }else if(JMXConnectionNotification.OPENED.equals(notifyType)){
            
        }else if(JMXConnectionNotification.FAILED.equals(notifyType)){
            
        }else if(JMXConnectionNotification.NOTIFS_LOST.equals(notifyType)){
            
        }else{
            logger.info("===================== 未知的通知类型 ===========================");
            logger.info("===================== "+notifyType+" ===========================");
        }
    }

}
