package com.auto.abstractrequest.chatmodule;

import com.error.IGameError;
import com.gamechannel.GameChannelContext;
import com.command.Command;
import com.command.ICommand;
import com.handler.ICommandHandler;
import com.auto.command.chatmodule.ChatCommandRequest;
import com.auto.command.chatmodule.ChatCommandResponse;


@Command(ChatCommandRequest.class)
public abstract class AbstractChatCommandHandler implements ICommandHandler{
	
	@Override
	public boolean verifyCommand0(ICommand command, GameChannelContext ctx) throws Exception {
		ChatCommandRequest request = (ChatCommandRequest)command;
		return verifyCommand(request.getContent(),ctx);
	}
	
	@Override
	public boolean action0(ICommand command, GameChannelContext ctx) throws Exception{ 
		ChatCommandRequest request = (ChatCommandRequest)command;
		return action(request.getContent(),ctx);
	}
	
	@Override
	public void returnResult0(ICommand command, GameChannelContext ctx) throws Exception{
		ChatCommandRequest request = (ChatCommandRequest)command;
		returnResult(request.getContent(), ctx);
	}
	
	public void sendCommand(String content,Integer pos,GameChannelContext ctx){
		ChatCommandResponse response = new ChatCommandResponse(content,pos);
		ctx.sendCommand(response);
	}
	
	protected void sendError(IGameError error,GameChannelContext ctx){
		ChatCommandResponse response = new ChatCommandResponse();
		ctx.sendCommand(response,error);
	}
	public abstract boolean verifyCommand(String content,GameChannelContext ctx) throws Exception;
	
	public abstract boolean action(String content,GameChannelContext ctx) throws Exception;
	
	public abstract void returnResult(String content,GameChannelContext ctx) throws Exception;
	
	
}