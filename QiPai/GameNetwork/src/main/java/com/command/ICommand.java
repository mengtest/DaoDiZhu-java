package com.command;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @Desc 描述:所有命令组成的接口，每个请求命令数据都要实现这个接口。
 * @author wang guang shuai
 * @Date 2016年9月17日 下午1:52:44
 */
public interface ICommand {
	

	byte[] write();

	void read(byte[] msg) throws InvalidProtocolBufferException;

	/**
	 * 
	 * @Desc 描述：获取命令id
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月17日 下午1:58:28
	 *
	 */
	int getCommandId();

	CommandHead getHead();

}
