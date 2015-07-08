package com.easeye.quartz.quartzmonitor.dao;

import java.sql.SQLException;
import java.util.List;

import com.easeye.quartz.quartzmonitor.exception.DBException;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;

public interface QuartzConfigDao {
	/**
	 * 获取所有的schedulers
	 * @return
	 * @throws DBException 
	 * @throws SQLException 
	 */
	public List<QuartzConfig> getALLQuartzConfigs() throws DBException, SQLException;
	/**
	 * 添加一个QuartzConfig
	 * @param QuartzConfig
	 * @throws DBException 
	 * @throws SQLException 
	 */
	public void addQuartzConfig(QuartzConfig config) throws DBException, SQLException;
	/**
	 * 更新一个QuartzConfig
	 * @param QuartzConfig
	 * @throws SQLException 
	 * @throws DBException 
	 */
	public void updateQuartzConfig(QuartzConfig config) throws DBException, SQLException;
	/**
	 * 删除一个QuartzConfig
	 * @param QuartzConfigId
	 * @throws SQLException 
	 * @throws DBException 
	 */
	public void deleteQuartzConfig(String configId) throws DBException, SQLException;
}
