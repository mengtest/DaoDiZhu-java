package com.common;

import java.text.ParseException;

import com.common.utils.DateUtil;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年10月27日 上午11:15:36
 */
public class Test {
	private static Object testLock = new Object();
	
	private static int count = 0;
	public static void main(String[] args) {
		
		int count = 2;
		if(count ==2){
			int times = 3;
			System.out.println(times);
		}
		
		System.out.println("执行成功");
	}

	public static void luaTest() {
		

	}
	
	public int max(int param1,int param2){
		int result = Math.max(param1, param2);
		return result;
	}

}
