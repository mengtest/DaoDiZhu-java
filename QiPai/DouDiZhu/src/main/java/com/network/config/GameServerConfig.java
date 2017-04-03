package com.network.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("GameServerConfig")
public class GameServerConfig {
	//验证是否登陆的地址
	private String loginCheckUrl;
	
	private int serverId;
	private String serverIp;
	private DataTimerConfig dataTimerConfig;


	public DataTimerConfig getDataTimerConfig() {
		return dataTimerConfig;
	}

	public void setDataTimerConfig(DataTimerConfig dataTimerConfig) {
		this.dataTimerConfig = dataTimerConfig;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getLoginCheckUrl() {
		return loginCheckUrl;
	}

	public void setLoginCheckUrl(String loginCheckUrl) {
		this.loginCheckUrl = loginCheckUrl;
	}
	
}
