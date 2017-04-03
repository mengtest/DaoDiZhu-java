package com.protocol.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("request")
public class RequestModel {

	@XStreamImplicit
	private List<VarModel> varList;

	public List<VarModel> getVarList() {
		return varList;
	}

	public void setVarList(List<VarModel> varList) {
		this.varList = varList;
	}
	
	
	
}
