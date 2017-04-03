package com.protocol.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
@XStreamAlias("Protocol")
public class ProtocolModel {
	@XStreamAsAttribute
	private String className;
	@XStreamImplicit
	private List<BeanModel> beanList;
	@XStreamImplicit
	private List<StructModel> structList;
	
	
	
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public List<BeanModel> getBeanList() {
		return beanList;
	}
	public void setBeanList(List<BeanModel> beanList) {
		this.beanList = beanList;
	}
	public List<StructModel> getStructList() {
		return structList;
	}
	public void setStructList(List<StructModel> structList) {
		this.structList = structList;
	}
	
	
}
