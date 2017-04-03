package com.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年9月21日 下午7:00:29
 */
public class ToolConfig {
	
	private Properties pro;
	private static ToolConfig instance = new ToolConfig();

	private ToolConfig() {
	}

	public static ToolConfig getInstance() {
		return instance;
	}
	
	public void init(){
		try {
			InputStream input = new FileInputStream(new File("config/tool.config"));
			pro = new Properties();
			pro.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getMysqlUrl(){
		String url =pro.getProperty("url"); 
		return url;
	}
	public String getMysqlUserName(){
		return pro.getProperty("username");
	}
	public String getMysqlPassword(){
		return pro.getProperty("password");
	}
	public String getJBDCDriver(){
		return pro.getProperty("jdbcDriver");
	}
	public String getMysqlTableCodePath(){
		return pro.getProperty("tableCodePath");
	}
	
	
}
