package com.codec;

import com.command.ICommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EncodeCommandHandler extends ChannelOutboundHandlerAdapter {

	private ICommandSerializer commandSerializer;

	public EncodeCommandHandler(ICommandSerializer commandSerializer) {
		this.commandSerializer = commandSerializer;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise arg2) throws Exception {
		if (msg == null) {
			return;
		}
		if (msg instanceof ICommand) {
			ICommand command = (ICommand)msg;
			ByteBuf byteBuf = commandSerializer.encode(command);
			ctx.writeAndFlush(byteBuf);
			arg2.setSuccess();
		}
	}

}
