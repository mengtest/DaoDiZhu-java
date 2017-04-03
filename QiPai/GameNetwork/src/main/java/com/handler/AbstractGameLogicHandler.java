package com.handler;

import com.gamechannel.GameChannelContext;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class AbstractGameLogicHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameChannelContext gameChannelContext = (GameChannelContext) msg;
		System.out.println("收到Command:" + gameChannelContext.getRequestCommand().getCommandId());
		this.channelRead(ctx, gameChannelContext);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		this.exceptionCatch(ctx, cause);
	}

	public abstract void channelRead(ChannelHandlerContext ctx,GameChannelContext gameChannelContext) throws Exception;
	public abstract void exceptionCatch(ChannelHandlerContext ctx, Throwable cause);
}
