package com.common.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.GeneratedMessage;
import com.googlecode.protobuf.format.JsonFormat;

/**
 * @Desc 描述:json串的管理工具类，负责json与对象之间的转换
 * @author wang guang shuai
 * @Date 2016年10月8日 下午5:29:46
 */
public class JsonUtil {
	
	
	/**
	 * 
	 * @Desc  描述：把对象转化为json串
	 * @param obj
	 * @return   
	 * @author wang guang shuai
	 * @date 2016年10月8日 下午5:42:46
	 *
	 */
	public static String objToJson(Object obj) {
		return JSON.toJSONString(obj);
	}
	/**
	 * 
	 * @Desc  描述：把json转化为对象
	 * @param json
	 * @param t
	 * @return   
	 * @author wang guang shuai
	 * @date 2016年10月8日 下午5:43:07
	 *
	 */
	public static <T> T jsonToObj(String json, Class<T> t) {
		return JSON.parseObject(json, t);
	}
	/**
	 * 
	 * 描述：把protobuf类转化为json
	 *
	 * @param message
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年10月26日 下午4:12:25
	 *
	 */
	public static String protoBufToJson(GeneratedMessage message){
		if(message == null){
			return null;
		}
		
		return JsonFormat.printToString(message);
	}
	/**
	 * 
	 * 描述：将protobuf的list对象集合转化为json
	 *
	 * @param list
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年11月2日 上午10:53:29
	 *
	 */
	public static String protobufListToJson(List<? extends GeneratedMessage> list){
		if(list == null || list.size() == 0){
			return "";
		}
		StringBuilder str = new StringBuilder();
		for(GeneratedMessage message : list){
			str.append(protoBufToJson(message)).append(" ");
		}
		return str.toString();
	}
}
