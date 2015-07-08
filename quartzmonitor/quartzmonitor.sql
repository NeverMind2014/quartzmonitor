-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.6.21-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  9.2.0.4947
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出 quartzmonitor 的数据库结构
DROP DATABASE IF EXISTS `quartzmonitor`;
CREATE DATABASE IF NOT EXISTS `quartzmonitor` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `quartzmonitor`;


-- 导出  表 quartzmonitor.t_job 结构
DROP TABLE IF EXISTS `t_job`;
CREATE TABLE IF NOT EXISTS `t_job` (
  `jobId` varchar(128) NOT NULL,
  `quartzConfigId` varchar(128) NOT NULL,
  `jobName` varchar(32) NOT NULL,
  `groupName` varchar(32) NOT NULL,
  `jobClass` varchar(256) NOT NULL,
  `jobDataMap` varchar(1024) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `durability` int(11) NOT NULL DEFAULT '0',
  `shouldRecover` int(11) NOT NULL DEFAULT '0',
  `triggerCount` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 quartzmonitor.t_quartz_config 结构
DROP TABLE IF EXISTS `t_quartz_config`;
CREATE TABLE IF NOT EXISTS `t_quartz_config` (
  `config_id` varchar(128) NOT NULL,
  `name` varchar(64) NOT NULL,
  `host` varchar(128) NOT NULL,
  `port` int(11) NOT NULL,
  `userName` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 quartzmonitor.t_trigger 结构
DROP TABLE IF EXISTS `t_trigger`;
CREATE TABLE IF NOT EXISTS `t_trigger` (
  `triggerId` varchar(128) NOT NULL,
  `jobId` varchar(128) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(50) DEFAULT NULL,
  `groupName` varchar(50) NOT NULL,
  `cronexpr` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
