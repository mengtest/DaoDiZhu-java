package com.config;

import com.common.utils.XmlUtil;

public class NetworkConfigFactory {

	private NetworkConfig networkConfig = null;
	
	private static NetworkConfigFactory instance = new NetworkConfigFactory();

	public static NetworkConfigFactory getInstance() {
		return instance;
	}
	/**
	 * 初始化配置信息
	 * @param path
	 */
	public void initNetworkConfig(String path){
		networkConfig = XmlUtil.xmlToBean(path, NetworkConfig.class);
	}
	
	public NetworkConfig getNetworkConfig(){
		if(networkConfig == null){
			throw new NullPointerException("NetworkConfig配置文件未初始化");
		}
		return networkConfig;
	}
}
