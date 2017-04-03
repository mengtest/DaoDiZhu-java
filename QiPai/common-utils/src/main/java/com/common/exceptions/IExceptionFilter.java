/**
 * 
 */
package com.common.exceptions;

/**
 * @Desc  描述:异常过滤器的统一接口
 * @author wang guang shuai
 * @Date 2016年9月17日 上午9:48:15
 */
public interface IExceptionFilter {
	/**
	 * 
	 * @Desc  描述：捕获某个异常的方法
	 * @param e
	 * @param msg   异常的额外信息
	 * @author wang guang shuai
	 * @date 2016年9月17日 上午9:54:28
	 *
	 */
	void exceptionCatch(Exception e,Object... msg);
	
}
