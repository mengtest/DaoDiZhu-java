package com.logic;

import com.command.ICommand;
import com.handler.AbstractLoginHandler;

import io.netty.channel.ChannelHandlerContext;

public class LoginHandler extends AbstractLoginHandler{
	
	private Long playerId = 0L;
	
	public LoginHandler() {
		
	}

	@Override
	public boolean verifyLogin(ICommand command, ChannelHandlerContext ctx) {
		
		return true;
	}

	@Override
	public Long getPlayerId() {
		
		return playerId;
	}

	@Override
	public int getWaiteLoginTime() {
		return 15;
	}

	@Override
	public int getLoginCommandId() {
		return 0;
	}

}
