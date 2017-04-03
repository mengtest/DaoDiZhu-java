package com.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("NetworkConfig")
public class NetworkConfig {

	private int port;
	private int bossThreads;
	private int workThreads;
	private int idleTime;
	private int waiteLoginTime;
	
	
	public int getWaiteLoginTime() {
		return waiteLoginTime;
	}
	public void setWaiteLoginTime(int waiteLoginTime) {
		this.waiteLoginTime = waiteLoginTime;
	}
	public int getIdleTime() {
		return idleTime;
	}
	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getBossThreads() {
		return bossThreads;
	}
	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}
	public int getWorkThreads() {
		return workThreads;
	}
	public void setWorkThreads(int workThreads) {
		this.workThreads = workThreads;
	}
	
	
	
}
