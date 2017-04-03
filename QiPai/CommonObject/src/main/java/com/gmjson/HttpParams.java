package com.gmjson;

import java.util.List;

public class HttpParams {
	private GmHttpType httpType;
	private Long userId;
	private Integer diamond;
	private List<Integer> userIds;
	
	
	
	public HttpParams(){}
	
	public HttpParams(GmHttpType httpType) {
		super();
		this.httpType = httpType;
	}
	public GmHttpType getHttpType() {
		return httpType;
	}
	public void setHttpType(GmHttpType httpType) {
		this.httpType = httpType;
	}
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getDiamond() {
		return diamond;
	}

	public void setDiamond(Integer diamond) {
		this.diamond = diamond;
	}

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
	
	
}
