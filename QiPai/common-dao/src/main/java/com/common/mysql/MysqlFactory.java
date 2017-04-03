package com.common.mysql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年12月8日 下午2:18:02
 */
public class MysqlFactory {

	private static MysqlClient client;

	public static void init(String configPath) throws IOException {
		Properties pro = new Properties();
		InputStream in = new FileInputStream(configPath);
		pro.load(in);
		client = new MysqlClient(pro);
	}
	
	public static MysqlClient getMysqlClient(){
		return client;
	}
}
