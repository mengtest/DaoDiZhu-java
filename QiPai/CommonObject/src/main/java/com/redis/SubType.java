package com.redis;
/**
 * redis中发布订阅的类型
 * @author wang guang shuai
 * @Date  2017年1月23日下午5:02:35
 */
public enum SubType {
	LOGIN_OUT(1,"退出"),
	PAY(2,"充值"),
	NOTICE(3,"公告");
	;
	private int type;
	private String desc;
	private SubType(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	public int getType() {
		return type;
	}
	public String getDesc() {
		return desc;
	}
	
	
}
