package com.protocol.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("bean")
public class BeanModel {
	@XStreamAlias("request")
	private RequestModel requestModel;
	@XStreamAlias("response")
	private ResponseModel responseModel;
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private String id;
	@XStreamAsAttribute
	private String desc;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public RequestModel getRequestModel() {
		return requestModel;
	}
	public void setRequestModel(RequestModel requestModel) {
		this.requestModel = requestModel;
	}
	public ResponseModel getResponseModel() {
		return responseModel;
	}
	public void setResponseModel(ResponseModel responseModel) {
		this.responseModel = responseModel;
	}
	
	
}
