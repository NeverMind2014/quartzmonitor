package com.easeye.quartz.quartzmonitor.service;

import java.sql.SQLException;
import java.util.List;

import com.easeye.quartz.quartzmonitor.exception.DBException;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;

public interface QuartzConfigService {
	/**
	 * 获取所有的configs
	 * @return
	 * @throws DBException 
	 * @throws SQLException 
	 */
	public List<QuartzConfig> getALLQuartzConfigs() throws DBException, SQLException;
	/**
	 * 添加一个config
	 * @param config
	 * @throws SQLException 
	 * @throws DBException 
	 */
	public void addQuartzConfig(QuartzConfig config) throws DBException, SQLException;
	/**
	 * 更新一个config
	 * @param config
	 * @throws SQLException 
	 * @throws DBException 
	 */
	public void updateQuartzConfig(QuartzConfig config) throws DBException, SQLException;
	/**
	 * 删除一个config
	 * @param configId
	 * @throws SQLException 
	 * @throws DBException 
	 */
	public void deleteQuartzConfig(String configId) throws DBException, SQLException;
}
