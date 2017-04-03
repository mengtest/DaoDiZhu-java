/**
 * 
 */
package com.common.redis;

import com.common.exceptions.AbstractExceptionFilter;

/**
 * @Desc 描述:这个是redis的异常过滤类，将来对redis的异常都在这处理
 * @author wang guang shuai
 * @Date 2016年9月17日 上午9:47:17
 */
public class RedisExceptionFilter extends AbstractExceptionFilter{
	private static RedisExceptionFilter instance = null;

	private RedisExceptionFilter() {
	}

	public static RedisExceptionFilter getInstance() {
		if (instance == null) {
			synchronized (RedisExceptionFilter.class) {
				if (instance == null) {
					instance = new RedisExceptionFilter();
				}
			}
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yhhx.exception.IExceptionFilter#exceptionCatch(java.lang.Exception)
	 */
	@Override
	public void exceptionCatch(Exception e,Object... msgs) {
		String value = this.formatMsg(msgs).toString();
		System.out.println(value);
		e.printStackTrace();
	}

}
