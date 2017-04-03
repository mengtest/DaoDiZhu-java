package com.mysql;

import com.config.ToolConfig;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年9月21日 下午6:53:01
 */
public class TableToObjectMain {

	public static void main(String[] args) {
		ToolConfig config = ToolConfig.getInstance();
		config.init();
		MySqlTableManager manager = new MySqlTableManager();
		manager.tableToObject();
	}
}
