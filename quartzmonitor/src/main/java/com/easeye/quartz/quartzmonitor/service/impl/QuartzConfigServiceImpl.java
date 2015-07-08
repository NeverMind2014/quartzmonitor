package com.easeye.quartz.quartzmonitor.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.easeye.quartz.quartzmonitor.conf.JdbcConfig;
import com.easeye.quartz.quartzmonitor.dao.QuartzConfigDao;
import com.easeye.quartz.quartzmonitor.dao.impl.QuartzConfigDaoImpl;
import com.easeye.quartz.quartzmonitor.exception.DBException;
import com.easeye.quartz.quartzmonitor.object.DataSourceInfo;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.service.QuartzConfigService;

public class QuartzConfigServiceImpl implements QuartzConfigService {

	private QuartzConfigDao schedulerDao;
	
	public QuartzConfigServiceImpl() {
		DataSourceInfo dsInfo = new DataSourceInfo();
		dsInfo.setConnurl(JdbcConfig.getJdbcUrl());
		dsInfo.setDriverclass(JdbcConfig.getJdbcDriverClass());
		dsInfo.setDsusername(JdbcConfig.getJdbcUserName());
		dsInfo.setDspassword(JdbcConfig.getJdbcPassword());
		
		schedulerDao = new QuartzConfigDaoImpl(dsInfo);
	}
    @Override
    public List<QuartzConfig> getALLQuartzConfigs() throws DBException, SQLException {
        return schedulerDao.getALLQuartzConfigs();
    }
    @Override
    public void addQuartzConfig(QuartzConfig config) throws DBException, SQLException {
        schedulerDao.addQuartzConfig(config);
    }
    @Override
    public void updateQuartzConfig(QuartzConfig config) throws DBException, SQLException {
        schedulerDao.updateQuartzConfig(config);
    }
    @Override
    public void deleteQuartzConfig(String configId) throws DBException, SQLException {
        schedulerDao.deleteQuartzConfig(configId);   
    }

}
