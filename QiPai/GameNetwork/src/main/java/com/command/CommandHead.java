package com.command;

import io.netty.buffer.ByteBuf;

/**
 * @Desc 描述:消息包的包头信息
 * @author wang guang shuai
 * @Date 2016年9月18日 上午9:30:01
 */
public class CommandHead {
	// 包头的总长度
	public  static int headLength = 24;
	private int commandId;
	private int clientSequenceId;
	private int serverSequenceId;
	private long sendTime;
	private int errorCode;

	public CommandHead() {
		super();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("commandId:").append(commandId).append(",").append(",Error:").append(errorCode).append("cId:")
				.append(clientSequenceId).append(",sId:").append(serverSequenceId);
		return str.toString();

	}

	public void setClientSequenceId(int clientSequenceId) {
		this.clientSequenceId = clientSequenceId;
	}

	public long getSendTime() {
		return sendTime;
	}

	/**
	 * 
	 * @Desc 描述：获取包头的总长度
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月18日 上午9:39:36
	 *
	 */
	public int getHeadLength() {
		return headLength;
	}

	/**
	 * 
	 * @Desc 描述：从缓冲区读取数据
	 * @param buffer
	 * @author wang guang shuai
	 * @date 2016年9月18日 上午9:33:47
	 *
	 */

	public void read(ByteBuf ioBuffer) {

		this.commandId = ioBuffer.readShort();
		this.clientSequenceId = ioBuffer.readInt();
		this.serverSequenceId = ioBuffer.readInt();
		this.sendTime = ioBuffer.readLong();
		this.errorCode = ioBuffer.readShort();
	}

	/**
	 * 
	 * @Desc 描述：向IoBuffer中写入包头的信息
	 * @param buffer
	 * @author wang guang shuai
	 * @date 2016年9月18日 上午9:37:13
	 *
	 */
	public int write(ByteBuf ioBuffer) {
		ioBuffer.writeShort(commandId);
		ioBuffer.writeInt(this.clientSequenceId);
		ioBuffer.writeInt(this.serverSequenceId);
		ioBuffer.writeLong(this.sendTime);
		ioBuffer.writeShort(errorCode);
		return headLength;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public static int getHeadlength() {
		return headLength;
	}

	public int getCommandId() {
		return commandId;
	}

	public int getClientSequenceId() {
		return clientSequenceId;
	}

	public int getServerSequenceId() {
		return serverSequenceId;
	}

	public void setServerSequenceId(int serverSequenceId) {
		this.serverSequenceId = serverSequenceId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	

}
