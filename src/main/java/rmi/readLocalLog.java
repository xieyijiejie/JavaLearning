package rmi;

import com.gbdata.common.util.DateUtil;
//import com.gbdata.dmc.workflow.RunHistory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
/*
 *传数据的shell脚本
 * #!/bin/bash
echo "start scp log form 108 and 22 " >> scp.log
scp root@180.178.46.108:/soft/metrix/tomcat/tomcat-7.0.52-18081/logs/localhost_access_log.$(date -d "1 days ago" +%Y-%m-%d).txt /home/elk/108_janus_logs/
scp root@182.16.61.22:/soft/metrix/tomcat/tomcat-7.0.52-18081/logs/localhost_access_log.$(date -d "1 days ago" +%Y-%m-%d).txt /home/elk/22_janus_logs/
echo "end scp log from 108 adn 22 " >> scp.log
echo "start scp log form 64 " >> scp.log
scp root@192.168.0.64:/opt/tomcat-18081/logs/localhost_access_log.$(date -d "1 days ago" +%Y-%m-%d).txt /home/elk/64_janus_logs/
echo "end scp log from 64 " >> scp.log
 */

public class readLocalLog extends readRemoteLog{
	  public static void main(String args[]) throws SQLException, MalformedURLException, RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException, NotBoundException{
//		  RunHistory info = RunHistory.parse(args);
//		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
//		  Boolean readall = (info == null || info.getInputParam("readall")==null || info.getInputParam("readall").getString().isEmpty())?false:Boolean.parseBoolean(info.getInputParam("readall").getString());
//		  String date = (info == null || info.getInputParam("date") == null || info.getInputParam("date").getString().isEmpty())?df.format(DateUtil.getYesterday()):info.getInputParam("date").getString();
//		  String ip = "192.168.0.65";
//		  readRemoteLog.readLog(readall, date, ip);
		  
		  String ip = "192.168.0.65";
		  readRemoteLog.readLog(Boolean.TRUE, "2018-07-11", ip);
	    }
}
