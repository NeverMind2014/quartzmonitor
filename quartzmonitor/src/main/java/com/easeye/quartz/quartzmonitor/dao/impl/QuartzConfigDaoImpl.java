package com.easeye.quartz.quartzmonitor.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.easeye.quartz.quartzmonitor.dao.QuartzConfigDao;
import com.easeye.quartz.quartzmonitor.exception.DBException;
import com.easeye.quartz.quartzmonitor.object.DataSourceInfo;
import com.easeye.quartz.quartzmonitor.object.QuartzConfig;
import com.easeye.quartz.quartzmonitor.util.DataSourceMapUtil;
import com.easeye.quartz.quartzmonitor.util.JdbcUtil;

public class QuartzConfigDaoImpl extends CommonDao implements QuartzConfigDao {

	public QuartzConfigDaoImpl(DataSourceInfo dsInfo) {
		super(dsInfo);
	}
	@Override
	public void addQuartzConfig(QuartzConfig config) throws DBException, SQLException {
		String sql = "INSERT INTO t_quartz_config (config_id, name, host, port, userName, password) ";
		sql += " values (?, ?, ?, ?, ?, ?)";
		
		JdbcUtil.operaterDebugSql(sql, config.getConfigId(), 
		        config.getName(), config.getPort(),
		        config.getUserName(), config.getPassword());
		
		executeSQL(sql, config.getConfigId(), 
		        config.getName(), config.getHost(), config.getPort(),
		        config.getUserName(), config.getPassword());
	}

    @Override
    public List<QuartzConfig> getALLQuartzConfigs() throws DBException, SQLException {
        String sql = "SELECT config_id AS configId, name, host, port, userName, password FROM t_quartz_config";
        
        JdbcUtil.operaterDebugSql(sql);
        
        QueryRunner queryRunner = new QueryRunner(true);
        Connection conn = null;
        List<QuartzConfig> list = new ArrayList<QuartzConfig>(0);
        
        try {
            conn = DataSourceMapUtil.getConnection(this.getDsObj());
            list = queryRunner.query(conn, sql, new BeanListHandler<QuartzConfig>(QuartzConfig.class));
            DbUtils.commitAndCloseQuietly(conn);
        } catch (Exception e) {
            throw new DBException("执行SQL: " + sql + "失败: " + e.getMessage(), e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        
        return list;
    }
    @Override
    public void updateQuartzConfig(QuartzConfig config) throws DBException, SQLException {
        String sql = "UPDATE t_quartz_config SET ";
        sql += "name = ?, ";
        sql += "host = ?, ";
        sql += "port = ?, ";
        sql += "userName = ?, ";
        sql += "password = ? where config_id = ?";
        
        JdbcUtil.operaterDebugSql(sql, config.getName(), config.getHost(), config.getPort(),
                config.getUserName(), config.getPassword(), config.getConfigId());
        
        executeSQL(sql, config.getName(), config.getHost(), config.getPort(),
                config.getUserName(), config.getPassword(), config.getConfigId());
    }
    @Override
    public void deleteQuartzConfig(String configId) throws DBException, SQLException {
        String sql = "DELETE FROM t_quartz_config WHERE config_id = ?";
        JdbcUtil.operaterDebugSql(sql, configId);
        executeSQL(sql, configId);
    }

}
