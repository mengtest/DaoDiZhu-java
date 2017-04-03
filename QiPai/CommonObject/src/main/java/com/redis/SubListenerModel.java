package com.redis;

import com.redis.SubType;

public class SubListenerModel {
	private int type;
	private long userId;
	private long playerId;
	private String serverId;
	private long createTime;
	private String content;
	private int diamon;
	
	
	public SubListenerModel() {
		super();
	}
	public SubListenerModel(SubType type) {
		super();
		this.type = type.getType();
	}
	
	public int getDiamon() {
		return diamon;
	}
	public void setDiamon(int diamon) {
		this.diamon = diamon;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	
}
