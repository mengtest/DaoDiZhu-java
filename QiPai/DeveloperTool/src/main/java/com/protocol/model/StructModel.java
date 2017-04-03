package com.protocol.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("struct")
public class StructModel {
	@XStreamAsAttribute
	private String name;
	@XStreamImplicit
	private List<VarModel> varList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<VarModel> getVarList() {
		return varList;
	}
	public void setVarList(List<VarModel> varList) {
		this.varList = varList;
	}
}
