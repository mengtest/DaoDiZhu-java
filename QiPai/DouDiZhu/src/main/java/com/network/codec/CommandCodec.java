package com.network.codec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codec.ICommandSerializer;
import com.command.CommandHead;
import com.command.CommandID;
import com.command.CommandType;
import com.command.ICommand;
import com.command.exceptions.CommandDegistException;
import com.command.exceptions.CommandLengthException;
import com.command.exceptions.CommandNotFundException;
import com.common.utils.Md5Utils;
import com.common.utils.ReflectUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class CommandCodec implements ICommandSerializer {

	private Map<Long, Class<? extends ICommand>> commandMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public void registerCommand(String commandPckPath) {
		List<Class<?>> clsList = ReflectUtil.getClasssFromPackage(commandPckPath);
		for (Class<?> cl : clsList) {
			CommandID commandAnno = cl.getAnnotation(CommandID.class);
			if (commandAnno != null) {
				int commandId = commandAnno.ID();
				CommandType commandType = commandAnno.type();
				long key = this.commandKey(commandId, commandType);
				commandMap.put(key, (Class<? extends ICommand>) cl);
			}
		}
	}
	/**
	 * 
	 * @Desc 给成一个唯一的命令id。 
	 * @param commandId
	 * @param type
	 * @return
	 * @throws CommandNotFundException
	 * @author 王广帅
	 * @Date 2017年3月4日 下午9:45:58
	 */
	private ICommand newCommand(int commandId, CommandType type) throws CommandNotFundException {
		long key = commandKey(commandId, type);
		Class<? extends ICommand> commandCl = commandMap.get(key);
		if (commandCl != null) {
			try {
				Object obj = commandCl.newInstance();
				return (ICommand) obj;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		throw new CommandNotFundException("命令找不到，ID:" + commandId + ",Type:" + type.name());
	}

	private long commandKey(int commandId, CommandType type) {
		long key = (((long) commandId) << 32) + type.ordinal();
		return key;
	}

	@Override
	public ByteBuf encode(ICommand command) {
		//这24个字节的长度是：包长度 + 包长度校验 + 数据接要的长度
		int total = command.getHead().getHeadLength() + 24;
		byte[] body = command.write();
		if(body != null){
			total += body.length;
		}
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer(total);
		byteBuf.writeInt(total);
		byteBuf.writeInt(this.checkCode(total));
		byteBuf.writeBytes(this.degist(body));
		command.getHead().write(byteBuf);
		byteBuf.writeBytes(body);
		return byteBuf;
	}
	
	private byte[] degist(byte[] body){
		return  Md5Utils.md5(body);
	}
	
	private int checkCode(int total){
		return total ^ 128 + 123;
	}

	@Override
	public ICommand decode(ByteBuf byteBuf) throws CommandLengthException, CommandNotFundException, InvalidProtocolBufferException, CommandDegistException {
		int total = byteBuf.readInt();
		int checkCode = byteBuf.readInt();
		if(checkCode != this.checkCode(total)){
			throw new CommandLengthException("网络数据解析错误，包长度不合法");
		}
		byte[] degist = new byte[16];
		byteBuf.readBytes(degist);
		CommandHead head = new CommandHead();
		head.read(byteBuf);
		ICommand command = this.newCommand(head.getCommandId(), CommandType.REQUEST);
		byte[] body = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(body);
		byte[] newDegist = this.degist(body);
		if(!Arrays.equals(degist, newDegist)){
			throw new CommandDegistException("网络数据包数字摘要失败");
		}
		command.read(body);
		return command;
	}

}
