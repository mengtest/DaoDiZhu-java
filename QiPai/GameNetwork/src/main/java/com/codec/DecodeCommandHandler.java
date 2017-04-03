package com.codec;

import com.command.ICommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DecodeCommandHandler extends ChannelInboundHandlerAdapter {
	private ICommandSerializer commandSerializer;
	
	public DecodeCommandHandler(ICommandSerializer commandSerializer){
		this.commandSerializer = commandSerializer;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object arg1) throws Exception {
		if (arg1 == null) {
			ctx.close();
			return;
		}
		ByteBuf byteBuf = (ByteBuf) arg1;
		ICommand command = commandSerializer.decode(byteBuf);
		if(command ==null){
			ctx.close();
			return;
		}
		ctx.fireChannelRead(command);
		
	}
	
	

}
