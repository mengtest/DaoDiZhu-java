package com.handler;

import com.command.ICommand;
import com.gamechannel.GameChannelContext;

/**
 * @Desc  描述:执行客户端请求命令的接口。
 * @author wang guang shuai
 * @Date 2016年9月17日 下午1:51:56
 */
public interface ICommandHandler {
	
	/**
	 * 
	 * @Desc  描述：用于验证command逻辑数据是否合法。
	 * @param command
	 * @param ctx
	 * @return   
	 * @author wang guang shuai
	 * @date 2016年9月18日 上午11:20:40
	 *
	 */
	boolean verifyCommand0(ICommand request,GameChannelContext ctx) throws Exception;
	/**
	 * 
	 * @Desc  描述：处理请求操作
	 * @param command
	 * @param ctx   
	 * @author wang guang shuai
	 * @throws Exception 
	 * @date 2016年9月18日 上午11:21:40
	 *
	 */
	boolean action0(ICommand request,GameChannelContext ctx) throws Exception;
	/**
	 * 
	 * 描述:返回给客户端的结果。
	 * @author wang guang shuai
	 * @Date   2016年12月14日下午6:20:30
	 * @param command
	 * @param ctx
	 */
	void returnResult0(ICommand request,GameChannelContext ctx) throws Exception;
}
