package com.gmjson;

public class BaseReturn {

	private int errorCode;
	private String desc = "";
	
	public BaseReturn(){}
	public BaseReturn(int errorCode,String desc){
		this.errorCode = errorCode;
		this.desc = desc;
	}
	public BaseReturn(IErrorCode errorCode){
		if(errorCode != null){
			this.errorCode = errorCode.getErrorCode();
			this.desc = errorCode.getDesc();
		}
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
