package com.network.config;

import com.common.utils.XmlUtil;

public class ConfigFactory {
	private static GameServerConfig gameServerConfig;
	
	public static void initGameServerConfig(String path){
		gameServerConfig = XmlUtil.xmlToBean(path, GameServerConfig.class);
	}

	public static GameServerConfig getGameServerConfig() {
		return gameServerConfig;
	}
	
	
}
