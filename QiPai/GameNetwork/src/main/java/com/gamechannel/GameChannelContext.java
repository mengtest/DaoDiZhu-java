package com.gamechannel;

import java.util.Map;

import com.command.CommandHead;
import com.command.ICommand;
import com.error.IGameError;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;

public class GameChannelContext {

	private ICommand requestCommand;

	private Long playerId;

	private int severSequenceId = 0;

	private EventLoop eventLoop;

	private Map<String, Object> managerMap;

	public GameChannelContext(Map<String, Object> managerMap, ICommand requestCommand, Long playerId,
			EventLoop eventLoop) {
		super();
		this.requestCommand = requestCommand;
		this.playerId = playerId;
		this.eventLoop = eventLoop;
		this.managerMap = managerMap;
	}

	public void execute(Runnable task) {
		eventLoop.execute(task);
	}

	public ICommand getRequestCommand() {
		return requestCommand;
	}

	public void setRequestCommand(ICommand requestCommand) {
		this.requestCommand = requestCommand;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * 给自己的客户端返回一条消息，这个消息可能带错误码
	 * 
	 * @param responseCommand
	 * @param gameError
	 */
	public void sendCommand(ICommand responseCommand, IGameError gameError) {
		setCommandHead(responseCommand, gameError);
		PlayerChannelGroup.getInstance().writeAndFlush(playerId, responseCommand);
	}

	/**
	 * 向客户端返回一条消息
	 * 
	 * @param responseCommand
	 */
	public void sendCommand(ICommand responseCommand) {
		setCommandHead(responseCommand, null);
		PlayerChannelGroup.getInstance().writeAndFlush(playerId, responseCommand);
	}

	private void setCommandHead(ICommand command, IGameError gameError) {
		CommandHead commandHead = command.getHead();
		if (commandHead == null) {
			commandHead = new CommandHead();
		}

		severSequenceId++;
		;
		commandHead.setServerSequenceId(severSequenceId);
		commandHead.setCommandId(command.getCommandId());
		commandHead.setClientSequenceId(requestCommand.getHead().getClientSequenceId());
		if (gameError != null) {
			commandHead.setErrorCode(gameError.getErrorCode());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getManager(Class<T> t) {
		return (T) managerMap.get(t.getSimpleName());
	}

}
