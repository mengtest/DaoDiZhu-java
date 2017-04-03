package com.command;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @Desc 描述:
 * @author wang guang shuai
 * @Date 2016年9月18日 上午10:25:28
 */
public abstract class AbstractCommand implements ICommand {
	private CommandHead commandHead = new CommandHead();

	/**
	 * @return the commandHead
	 */
	@Override
	public CommandHead getHead() {
		return commandHead;
	}

	@Override
	public byte[] write() {

		GeneratedMessage generatedMessage = this.getGenerateMessage();
		if (generatedMessage != null) {
			byte[] body = generatedMessage.toByteArray();
			return body;
		}

		return null;
	}

	@Override
	public void read(byte[] bodyBytes) throws InvalidProtocolBufferException {
			this.parseFromBytes(bodyBytes);
	}
	/**
	 * 
	 * @Desc 描述：从bytes数组中读取数据，这个是protobuf实现的，由子类实现。
	 * @param bytes
	 * @author wang guang shuai
	 * @throws InvalidProtocolBufferException
	 * @date 2016年9月18日 上午10:38:03
	 *
	 */
	protected abstract void parseFromBytes(byte[] bytes) throws InvalidProtocolBufferException;

	/**
	 * 
	 * @Desc 描述：把包体使用protobuf序列化，并返回序列化的管理对象。
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月18日 上午10:38:44
	 *
	 */
	protected abstract GeneratedMessage getGenerateMessage();

}
