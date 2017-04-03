/**
 * 
 */
package com.common.exceptions;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年9月17日 上午9:55:28
 */
public abstract class AbstractExceptionFilter implements IExceptionFilter{
	/**
	 * 
	 * @Desc  描述：将异常信息格式化输出
	 * @param msgs
	 * @return   
	 * @author wang guang shuai
	 * @date 2016年9月17日 上午9:59:09
	 *
	 */
	public StringBuilder formatMsg(Object... msgs){
		StringBuilder msgStr = new StringBuilder();
		if(msgs != null){
			for(Object msg : msgs){
				msgStr.append(msg).append(" ");
			}
		}
		return msgStr;
	}
}
