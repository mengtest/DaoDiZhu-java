package com.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年10月8日 下午3:17:40
 */
public class CollectionUtil {
	public static <K,V> LinkedHashMap<K, V>	 createLinkeHashMap(int cacheSize){
		LinkedHashMap<K, V>	linkedHashMap = new  LinkedHashMap<K,V>(cacheSize,0.75f,true){
			private static final long serialVersionUID = 1L;
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				if (this.size() > cacheSize) {
					return true;
				}
				return false;
			}
		};
		return linkedHashMap;
	}
}
