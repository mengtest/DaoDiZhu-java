package com.gmjson;

public enum GmHttpType {
	ADD_DIAMOND(1,"添加钻石"),
	GET_USER_DIAMOND(2,"获取用户钻石数"),
	GAME_USER_IS_EXIST(3,"用户是否存在"),
	
	
	;
	private int requestId;
	private String desc;
	
	private GmHttpType(int requestId,String desc) {
		this.requestId = requestId;
		this.desc = desc;
	}

	public int getRequestId() {
		return requestId;
	}

	public String getDesc() {
		return desc;
	}
	
	
}
