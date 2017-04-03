package com.logic;

import com.gamechannel.GameChannelContext;
import com.handler.AbstractGameLogicHandler;

import io.netty.channel.ChannelHandlerContext;

public class GameUserHandler extends AbstractGameLogicHandler {

	@Override
	public void exceptionCatch(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, GameChannelContext gameChannelContext) throws Exception {
		
	}

}
