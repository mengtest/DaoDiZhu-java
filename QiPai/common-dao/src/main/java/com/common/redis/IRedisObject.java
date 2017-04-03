/**
 * 
 */
package com.common.redis;

import java.util.Map;

/**
 * @Desc 描述:这个接口是所有要操作redis的数据对象的接口，它负责一些redis的公共操作。 每一个对象对应一个redis中的hashmap类型。
 * @author wang guang shuai
 * @Date 2016年9月16日 下午8:38:08
 */
public interface IRedisObject {
	/**
	 * 
	 * @Desc 描述：获取存储在redis对象的key,这个key一般对应的是redis中存储一个hashmap的key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:39:40
	 *
	 */
	String getRedisKey();

	

	/**
	 * 
	 * @Desc 描述：获取对象中所有字段组成的Map
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:40:50
	 *
	 */
	Map<String, String> getAllValueMap();

	/**
	 * 
	 * @Desc 描述：获取一个对象中要修改的字段的map，每次对字段set的时候，会标记为修改。
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:42:31
	 *
	 */
	Map<String, String> getUpdateValueMap();

	/**
	 * 
	 * @Desc 描述：从map中获取所有字段的数据
	 * @param map
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:47:03
	 *
	 */
	void setValueFromMap(Map<String, String> map);
}
