package com.common.redis;

import com.common.redis.RedisClient;
import com.common.redis.RedisConfig;
import com.common.utils.XmlUtil;

/**
 * 描述:redis工厂，负责redis的初始化与客户端返回
 * @author wang guang shuai
 * 2016年12月7日 下午6:36:23
 */
public class RedisFactory {

	private static  RedisClient redisClient;
	
	public static void initRedis(String config){
		RedisConfig redisConfig = XmlUtil.xmlToBean(config, RedisConfig.class);
		redisClient = new RedisClient(redisConfig);
		
	}
	
	public static RedisClient getRedis(){
		return redisClient;
	}
}
