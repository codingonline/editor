-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 01, 2016 at 07:41 PM
-- Server version: 5.5.47-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pop2016`
--

-- --------------------------------------------------------

--
-- Table structure for table `app`
--

CREATE TABLE IF NOT EXISTS `app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL DEFAULT '',
  `app_type` varchar(32) NOT NULL DEFAULT '',
  `user_name` varchar(64) NOT NULL DEFAULT '',
  `owner_name` varchar(64) NOT NULL DEFAULT '',
  `write` tinyint(1) DEFAULT '1',
  `paas_name` varchar(32) DEFAULT '',
  `svn_url` varchar(256) DEFAULT '',
  `git_url` varchar(256) DEFAULT '',
  `domain` varchar(256) DEFAULT '',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `import_url` varchar(64) DEFAULT '',
  `save_data` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `app_name` (`app_name`),
  KEY `username_app` (`user_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1124 ;

--
-- Dumping data for table `app`
--

-- --------------------------------------------------------

--
-- Triggers `app`
--
DROP TRIGGER IF EXISTS `addauth`;
DELIMITER //
CREATE TRIGGER `addauth` AFTER INSERT ON `app`
 FOR EACH ROW begin
  if new.owner_name=new.user_name and new.owner_name!='guest' then 
    insert into rwfileauth(path, username, apptype, ownername) values(concat("/",new.app_name,"/"), new.owner_name, new.app_type, new.owner_name);
  end if;
 end
//
DELIMITER ;
DROP TRIGGER IF EXISTS `delauth`;
DELIMITER //
CREATE TRIGGER `delauth` BEFORE DELETE ON `app`
 FOR EACH ROW begin
  if old.owner_name=old.user_name then 
  	delete from rwfileauth where path=concat("/",old.app_name,"/") and ownername=old.owner_name and apptype=old.app_type;
  end if;
 end
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `app_code`
--

CREATE TABLE IF NOT EXISTS `app_code` (
  `id` int(11) NOT NULL,
  `username` varchar(64) NOT NULL,
  `appname` varchar(256) NOT NULL,
  `web_url` varchar(256) NOT NULL,
  `organization_type` varchar(256) NOT NULL,
  `owner_name` varchar(256) NOT NULL,
  `git_url` varchar(256) NOT NULL,
  `http_url` varchar(256) NOT NULL,
  `is_push` varchar(5) NOT NULL,
  `is_downloaded` varchar(5) NOT NULL DEFAULT 'false',
  `is_public` varchar(5) NOT NULL DEFAULT 'false',
  PRIMARY KEY (`id`),
  KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `app_code`
--

-- --------------------------------------------------------

--
-- Table structure for table `app_instance`
--

CREATE TABLE IF NOT EXISTS `app_instance` (
  `dockerid` varchar(130) NOT NULL COMMENT '运行应用所在的docker id',
  `domain` varchar(100) NOT NULL COMMENT '运行容器所在的主机ip或者域名',
  `port` int(11) NOT NULL COMMENT '应用映射到的端口',
  `sshport` int(11) NOT NULL COMMENT 'ssh 所使用的端口号',
  `appid` int(11) NOT NULL COMMENT '对应的app的id',
  PRIMARY KEY (`dockerid`),
  KEY `appid` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `app_instance`
--


-- --------------------------------------------------------

--
-- Table structure for table `communication`
--

CREATE TABLE IF NOT EXISTS `communication` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `time` datetime NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=49 ;

--
-- Dumping data for table `communication`
--

-- --------------------------------------------------------

--
-- Table structure for table `completion`
--

CREATE TABLE IF NOT EXISTS `completion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class` varchar(128) NOT NULL,
  `method` varchar(64) NOT NULL,
  `count` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `completion`
--

-- --------------------------------------------------------

--
-- Table structure for table `dockerstat`
--

CREATE TABLE IF NOT EXISTS `dockerstat` (
  `dockerid` varchar(12) NOT NULL,
  `time` datetime NOT NULL,
  `cpu` float DEFAULT NULL,
  `memuse` float DEFAULT NULL,
  `memall` float DEFAULT NULL,
  `mempercent` float DEFAULT NULL,
  `netin` float DEFAULT NULL,
  `netout` float DEFAULT NULL,
  PRIMARY KEY (`dockerid`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `dockerstat`
--

-- --------------------------------------------------------

--
-- Table structure for table `home_service`
--

CREATE TABLE IF NOT EXISTS `home_service` (
  `service_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_name` varchar(45) NOT NULL,
  `service_type` varchar(45) NOT NULL,
  PRIMARY KEY (`service_id`),
  UNIQUE KEY `service_name_UNIQUE` (`service_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `home_service`
--

-- --------------------------------------------------------

--
-- Table structure for table `home_service_instance`
--

CREATE TABLE IF NOT EXISTS `home_service_instance` (
  `dockerid` varchar(12) NOT NULL,
  `service_id` int(11) NOT NULL,
  `domain` varchar(45) NOT NULL,
  `port` int(11) NOT NULL,
  `node` int(11) NOT NULL,
  `sshport` int(11) NOT NULL,
  `create_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `home_service_instance`
--

-- --------------------------------------------------------

--
-- Table structure for table `issue`
--

CREATE TABLE IF NOT EXISTS `issue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `issue_type` varchar(45) DEFAULT NULL,
  `issue_head` varchar(100) DEFAULT NULL,
  `issue_body` text NOT NULL,
  `email` varchar(100) NOT NULL,
  `is_deal` tinyint(1) DEFAULT '0',
  `solution` text,
  `attachment` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=63 ;

--
-- Dumping data for table `issue`
--

-- --------------------------------------------------------

--
-- Table structure for table `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(60) DEFAULT NULL,
  `action` varchar(32) DEFAULT NULL,
  `domain` varchar(30) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `dockerid` varchar(20) DEFAULT NULL,
  `ptype` varchar(32) DEFAULT NULL,
  `owner` varchar(32) DEFAULT NULL,
  `user` varchar(32) DEFAULT NULL,
  `appname` varchar(32) DEFAULT NULL,
  `deploy_time` varchar(40) DEFAULT NULL,
  `action_time` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=327 ;

--
-- Dumping data for table `log`
--

-- --------------------------------------------------------

--
-- Table structure for table `online_user`
--

CREATE TABLE IF NOT EXISTS `online_user` (
  `date` date NOT NULL,
  `user_num` int(11) DEFAULT NULL,
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `online_user`
--

-- --------------------------------------------------------

--
-- Table structure for table `rwfileauth`
--

CREATE TABLE IF NOT EXISTS `rwfileauth` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(512) NOT NULL,
  `username` varchar(64) NOT NULL,
  `action_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ownername` varchar(128) NOT NULL,
  `apptype` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `uname_fk` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=964 ;

--
-- Dumping data for table `rwfileauth`
--

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

CREATE TABLE IF NOT EXISTS `service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `service_name` varchar(32) NOT NULL,
  `service_type` varchar(32) NOT NULL,
  `service_container_type` varchar(32) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `description` text,
  `plugin_address` varchar(32) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `update_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `owner_name` varchar(32) DEFAULT NULL,
  `owner_phone` varchar(32) DEFAULT NULL,
  `owner_qq` varchar(32) DEFAULT NULL,
  `owner_wechat` varchar(32) DEFAULT NULL,
  `issuper` tinyint(1) DEFAULT '0',
  `status` int(11) DEFAULT '0' COMMENT '审核状态\n0-待审核\n1-通过审核\n2-未通过审核',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=31 ;

--
-- Dumping data for table `service`
--

-- --------------------------------------------------------

--
-- Table structure for table `service_authorization`
--

CREATE TABLE IF NOT EXISTS `service_authorization` (
  `authorizationid` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `serviceid` int(11) NOT NULL,
  `authorize_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`authorizationid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=55 ;

--
-- Dumping data for table `service_authorization`
--

-- --------------------------------------------------------

--
-- Table structure for table `service_instance`
--

CREATE TABLE IF NOT EXISTS `service_instance` (
  `dockerid` varchar(130) NOT NULL COMMENT '运行应用所在的docker id',
  `domain` varchar(100) NOT NULL COMMENT '运行容器所在的主机ip或者域名',
  `port` int(11) NOT NULL COMMENT '应用映射到的端口',
  `sshport` int(11) NOT NULL COMMENT 'ssh 所使用的端口号',
  `service_id` int(11) NOT NULL COMMENT '对应的service的id',
  PRIMARY KEY (`dockerid`),
  KEY `service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `service_instance`
--

-- --------------------------------------------------------

--
-- Table structure for table `service_router`
--

CREATE TABLE IF NOT EXISTS `service_router` (
  `dockerid` varchar(130) NOT NULL COMMENT '运行应用所在的docker id',
  `domain` varchar(100) NOT NULL COMMENT '运行容器所在的主机ip或者域名',
  `port` int(11) NOT NULL COMMENT '应用映射到的端口',
  `sshport` int(11) NOT NULL COMMENT 'ssh 所使用的端口号',
  `service_id` int(11) NOT NULL COMMENT '对应的service的id',
  PRIMARY KEY (`dockerid`),
  KEY `service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `service_router`
--

-- --------------------------------------------------------

--
-- Table structure for table `share`
--

CREATE TABLE IF NOT EXISTS `share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `appname` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `token` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `username_share` (`username`),
  KEY `appname_share` (`appname`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=23 ;

--
-- Dumping data for table `share`
--

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT '',
  `password` varchar(128) DEFAULT '',
  `last_login` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_super` tinyint(1) DEFAULT '0',
  `email` varchar(254) DEFAULT '',
  `date_joined` datetime DEFAULT '2015-07-27 00:00:00',
  `uname_baidu` varchar(64) DEFAULT '',
  `uname_sina` varchar(64) DEFAULT '',
  `uname_sae` varchar(64) DEFAULT '',
  `uname_code` varchar(64) DEFAULT '',
  `pwd_baidu` varchar(256) DEFAULT '',
  `pwd_sae` varchar(256) DEFAULT '',
  `register_time` datetime DEFAULT '2015-07-27 00:00:00',
  `validate_code` varchar(64) DEFAULT '',
  `register_status` tinyint(1) DEFAULT '0',
  `token` varchar(32) NOT NULL DEFAULT '',
  `is_remembered` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `username` (`username`),
  KEY `uname_code` (`uname_code`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=445 ;

--
-- Dumping data for table `user`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_tenxcloud`
--

CREATE TABLE IF NOT EXISTS `user_tenxcloud` (
  `username` varchar(64) NOT NULL,
  `username_tenxcloud` varchar(64) DEFAULT NULL,
  `token` varchar(64) DEFAULT NULL,
  `service_address` varchar(256) DEFAULT NULL,
  `user_status` tinyint(4) DEFAULT '0',
  `service_type` varchar(32) DEFAULT NULL,
  `service_name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `username_tenxcloud` (`username_tenxcloud`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='tenxcloud user info';

--
-- Dumping data for table `user_tenxcloud`
--

-- --------------------------------------------------------

--
-- Constraints for dumped tables
--

--
-- Constraints for table `app`
--
ALTER TABLE `app`
  ADD CONSTRAINT `username_app` FOREIGN KEY (`user_name`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `app_code`
--
ALTER TABLE `app_code`
  ADD CONSTRAINT `username` FOREIGN KEY (`username`) REFERENCES `user` (`uname_code`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `app_instance`
--
ALTER TABLE `app_instance`
  ADD CONSTRAINT `appid` FOREIGN KEY (`appid`) REFERENCES `app` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `rwfileauth`
--
ALTER TABLE `rwfileauth`
  ADD CONSTRAINT `uname_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `service_instance`
--
ALTER TABLE `service_instance`
  ADD CONSTRAINT `service_id` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`);

--
-- Constraints for table `share`
--
ALTER TABLE `share`
  ADD CONSTRAINT `appname_share` FOREIGN KEY (`appname`) REFERENCES `app` (`app_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `username_share` FOREIGN KEY (`username`) REFERENCES `app` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
