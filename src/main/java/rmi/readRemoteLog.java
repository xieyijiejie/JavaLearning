package rmi;

import com.mongodb.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class readRemoteLog {
	public static Map<String, Integer> map = new HashMap<String, Integer>();
	public static Map<String, Integer> map_split = new HashMap<String, Integer>();
	public static String URL = "jdbc:sqlserver://192.168.0.68:1433;databaseName=gbdatabase";
	public static String USERNAME = "app_login_system";
	public static String PASSWORD = "gaimimadeshiwangbadan123!";
      
	public static void readLog(Boolean readall,String date, String ip) throws SQLException, ParseException, MalformedURLException, RemoteException, NotBoundException, NoSuchAlgorithmException, UnsupportedEncodingException{
		Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    Statement statemenet = connection.createStatement();
	    String table = "dbo.MetrixJanusTraking_Lcoal";
        	log janus_log =(log) Naming.lookup("rmi://192.168.0.65:8888/janus_log");
        	String[] files = new String[1];
        	String path = "/home/elk/64_janus_logs/";
        	if(ip.equals("182.16.61.22")){
        		 path = "/home/elk/22_janus_logs/";
        		 table = "dbo.MetrixJanusTraking";
        	}else if(ip.equals("180.178.46.108")){
        		 path = "/home/elk/108_janus_logs/";
        		 table = "dbo.MetrixJanusTraking";
        	}
        		
        	if(readall)
        		files = janus_log.getFiles(path);
        	else 
        		files[0] = "localhost_access_log." + date + ".txt";
			
        	int num = 0;
            for(String file_name:files){
            	if(!file_name.contains("localhost_access_log"))
            		continue;
            	List<String> content = janus_log.read(path + file_name);//D:\\intellJcode\\log\\localhost_access_log.2016-04-13.txt
            	for(String line:content){
  	            	Map<String,String> map = new HashMap<String,String>();
  	            	String [] log = line.split(" ");
  	            	MessageDigest md;
  	            	
  	            	md = MessageDigest.getInstance("MD5");
  	            	md.update(line.getBytes());
  	            	map.put("MD5", Util.toHex(md.digest()));
  	            	try{
  	            		map.put("Request",  java.net.URLDecoder.decode(log[6], "utf-8").replace("'", ""));
  	            	}catch (IllegalArgumentException e){
  	            		System.out.println("there has a  java.lang.IllegalArgumentException in decode log");
  	            	}
  	            	map.put("Ip", log[0]);
  	            	map.put("Status", log[8]);
  	            	map.put("ResponseByte", log[9]);
  	            	Matcher matcher = Pattern.compile("\\d+").matcher(log[10]);
  	            	if(matcher.find())
  	            	map.put("ResponseTime", matcher.group(0));
  	            	String log_date = log[3].replace("[", "");

  	            	//Z 对于格式化来说，使用 RFC 822 4-digit 时区格式 ,Locale.US表示使用了美国时间
  	            	SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
  	            	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	            	log_date = sdf2.format(sdf.parse(log_date));
  	            	map.put("Trackdate", log_date);

  	            	List<String> keys = Arrays.asList("MD5","Ip","Trackdate","Request", "Status","ResponseByte","ResponseTime");
  	          
  	            	String value = "";
  	            	for(String key:keys){
//  	            		condition = condition.concat(key + "='"+ map.get(key) + "',");
  	            		value = value.concat("'"+ map.get(key) + "',");
  	            	}
  	            	String insert = "insert into " + table + " values (" + value.substring(0, value.length()-1) + ")";
//  	            	String update = "update " + table + " set " + condition.substring(0, condition.length()-1) + " where MD5='" + map.get("MD5") + "'";
  	                try{
  	                	statemenet.executeUpdate(insert);
  	                	num++;
  	                	if(num%1000 == 0)
  	                		System.out.println("新添加了1000条数据，共添加了" + num + "条数据！");
  	                } catch(com.microsoft.sqlserver.jdbc.SQLServerException e) { 
//  	                  System.out.println("添加失败！数据已存在！");
  	              }
  	            } 
            }
            statemenet.close();
         	connection.close();
        } 
}
