package com.codec;

import com.command.ICommand;
import com.command.exceptions.CommandDegistException;
import com.command.exceptions.CommandLengthException;
import com.command.exceptions.CommandNotFundException;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;

public interface ICommandSerializer {

	/**
	 * 编码，将一个命令转为一个bytebuf。
	 * @param command
	 * @return
	 */
	ByteBuf encode(ICommand command);
	/**
	 * 解码一个bytebuf，如果bytebuf是一个对象池分配的，那么这个方法的实现者负责释放bytebuf。
	 * @param byteBuf
	 * @return
	 * @throws CommandLengthException 
	 * @throws CommandNotFundException 
	 * @throws InvalidProtocolBufferException 
	 * @throws CommandDegistException 
	 */
	ICommand decode(ByteBuf byteBuf) throws CommandLengthException, CommandNotFundException, InvalidProtocolBufferException, CommandDegistException;
	
	
}
