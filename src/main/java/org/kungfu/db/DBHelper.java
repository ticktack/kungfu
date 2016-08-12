package org.kungfu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBHelper {
	    public static final String driver = "com.mysql.jdbc.Driver";  
	   
	  
	    public Connection conn = null;  
	    public PreparedStatement pst = null;  
	  
	    public String detectConnect(String ip,String dbname, String user, String password) {  
	        try {  
	            Class.forName(driver);//指定连接类型  
	            DriverManager.setLoginTimeout(5);
	            conn = DriverManager.getConnection("jdbc:mysql://" + ip + "/"+ dbname, user, password);//获取连接
	           
	            conn.close();
	            return null;
	        } catch (Exception e) {  
	            return e.getMessage();
	        }  
	    }  
	  
	 
}
