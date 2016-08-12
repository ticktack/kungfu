package org.kungfu.db;


import java.sql.SQLException;

import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.c3p0.C3p0Plugin;

public class DataSourceKit {
	
	private static String getJdbcUrl(String ip, String dbName) {
		return "jdbc:mysql://" + ip + "/" + dbName + "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
	}
	
	// 调用后即可使用Db.use(dbName+ip).find(sql) 
	public static String instanceDataSource(String ip, String dbName, String username, String password) {
		// 根据ip和数据库名使用数据源
		String configName = dbName + ip;
		Config config = DbKit.getConfig(configName);
		if (config == null) {
			C3p0Plugin c3p0Plugin = org.kungfu.core.Config.createC3p0Plugin( getJdbcUrl(ip, dbName), username, password.trim());
			c3p0Plugin.start();
			config = new Config(dbName+ip, c3p0Plugin.getDataSource());
			DbKit.addConfig(config); 
		}
		return configName;
	}
	
	public static void colseDataSource(String configName) {
		
		Config config = DbKit.getConfig(configName);
		try {
			if (config != null && !config.getDataSource().getConnection().isClosed()) {
				config.getDataSource().getConnection().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DbKit.removeConfig(configName);
	}

}
