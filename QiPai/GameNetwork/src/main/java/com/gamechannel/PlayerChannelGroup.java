package com.gamechannel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.command.ICommand;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;

public class PlayerChannelGroup {

	private EventLoopGroup eventLoopGroup = null;
	/**
	 * 记录用户id与channelid的对应关系。
	 */
	private ConcurrentHashMap<Long, ChannelContext> PLAYER_ID_CHANNEL_ID = new ConcurrentHashMap<>();

	private DefaultChannelGroup channelGroup = new DefaultChannelGroup(
			new DefaultEventExecutor(Executors.newSingleThreadExecutor()));
	private static PlayerChannelGroup instance = new PlayerChannelGroup();

	private PlayerChannelGroup() {
	}

	public static PlayerChannelGroup getInstance() {
		return instance;
	}

	public void setEventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
	}

	private ChannelId getChannelIdByPlayerId(Long playerId) {
		ChannelContext channelContext = PLAYER_ID_CHANNEL_ID.get(playerId);
		if (channelContext != null) {
			return channelContext.getChannelId();
		}
		return null;
	}
	
	public EventLoop getEventLoop(Long playerId){
		ChannelContext channelContext = PLAYER_ID_CHANNEL_ID.get(playerId);
		if (channelContext != null) {
			return channelContext.getEventLoop();
		}
		return null;
	}

	/**
	 * 
	 * 描述:添加一个新的channel连接。
	 * 
	 * @author wang guang shuai
	 * @Date 2017年2月5日上午12:59:54
	 * @param playerId
	 * @param ctx
	 */
	public void addChannel(Long playerId, Channel channel) {
		ChannelContext channelContext = PLAYER_ID_CHANNEL_ID.get(playerId);
		if (channelContext == null) {
			channelContext = new ChannelContext(channel.id(), eventLoopGroup.next());
			PLAYER_ID_CHANNEL_ID.put(playerId, channelContext);
		} else {
			channelContext.setChannelId(channel.id());
		}

		channelGroup.add(channel);
	}

	/**
	 * 
	 * 描述:移除一个channel。
	 * 
	 * @author wang guang shuai
	 * @Date 2017年2月5日上午12:59:34
	 * @param ctx
	 */
	public void removeChannel(Channel channel) {
		channelGroup.remove(channel.id());
	}

	/**
	 * 
	 * 描述:移除一个用户与channelid的对应关系
	 * 
	 * @author wang guang shuai
	 * @Date 2017年2月5日上午12:58:55
	 * @param playerId
	 */
	public void removePlayerId(Long playerId) {
		this.PLAYER_ID_CHANNEL_ID.remove(playerId);
	}

	/**
	 * 
	 * 描述:判断某个连接是否存在。
	 * 
	 * @author wang guang shuai
	 * @Date 2017年2月5日上午1:03:00
	 * @param channel
	 * @return
	 */
	public boolean containsChannel(Channel channel) {
		return channelGroup.contains(channel);
	}

	/**
	 * 
	 * 描述:向某个用户发送一条命令
	 * 
	 * @author wang guang shuai
	 * @Date 2017年2月5日上午12:42:39
	 * @param playerId
	 * @param command
	 */
	protected void writeAndFlush(Long playerId, ICommand command) {
		ChannelId id = this.getChannelIdByPlayerId(playerId);
		if (id != null) {
			Channel channel = channelGroup.find(id);
			channel.writeAndFlush(command);
		}
	}

	/**
	 * 给所有连接广播一条命令
	 * 
	 * @param command
	 */
	public void broadcast(ICommand command) {
		channelGroup.writeAndFlush(command);
	}

	/**
	 * 给某些用户广播一条命令。
	 * 
	 * @param playerIds
	 * @param command
	 */
	public void broadcast(List<Long> playerIds, ICommand command) {
		for (Long playerId : playerIds) {
			writeAndFlush(playerId, command);
		}
	}

	public static class ChannelContext {
		private ChannelId channelId;
		private EventLoop eventLoop;

		public ChannelContext(ChannelId channelId, EventLoop eventLoop) {
			super();
			this.channelId = channelId;
			this.eventLoop = eventLoop;
		}

		public ChannelId getChannelId() {
			return channelId;
		}

		public void setChannelId(ChannelId channelId) {
			this.channelId = channelId;
		}

		public EventLoop getEventLoop() {
			return eventLoop;
		}

	}

}
