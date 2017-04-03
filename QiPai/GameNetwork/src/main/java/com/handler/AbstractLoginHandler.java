package com.handler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.command.ICommand;
import com.gamechannel.GameChannelContext;
import com.gamechannel.PlayerChannelGroup;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;

public abstract class AbstractLoginHandler extends ChannelInboundHandlerAdapter {

	private long playerId = 0;
	private EventLoop eventLoop;
	private Map<String, Object> managerMap;

	public AbstractLoginHandler() {
	}

	public void setManagerMap(Map<String, Object> managerMap) {
		this.managerMap = managerMap;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 当连接建立后，执行一个时间的一次性定时任务，判断有没有登陆，如果未登陆，连接断开。
		ctx.executor().schedule(new CheckLoginTask(ctx.channel()), getWaiteLoginTime(), TimeUnit.SECONDS);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg == null) {

			return;
		}
		ICommand command = (ICommand) msg;
		if (getLoginCommandId() == command.getCommandId()) {
			if (verifyLogin(command, ctx)) {
				playerId = this.getPlayerId();
				PlayerChannelGroup.getInstance().addChannel(playerId, ctx.channel());
			}
		} else {
			if (eventLoop == null) {
				eventLoop = PlayerChannelGroup.getInstance().getEventLoop(playerId);
			}
			GameChannelContext gameChannelContext = new GameChannelContext(managerMap, command, playerId, eventLoop);
			ctx.fireChannelRead(gameChannelContext);
		}
	}

	/**
	 * 
	 * @Desc 返回客户端与服务器建立连接成功后，等待登陆执行成功的时间。如果在这个时间之后，客户端还没有登陆成功。则连接自动断开。
	 * @return
	 * @author 王广帅
	 * @Date 2017年3月5日 下午12:06:15
	 */
	public abstract int getWaiteLoginTime();

	/**
	 * 
	 * @Desc 返回登陆命令的id。
	 * @return
	 * @author 王广帅
	 * @Date 2017年3月5日 下午12:02:20
	 */
	public abstract int getLoginCommandId();

	/**
	 * 验证登陆是否成功
	 * 
	 * @param command
	 * @param ctx
	 * @return
	 */
	public abstract boolean verifyLogin(ICommand command, ChannelHandlerContext ctx);

	/**
	 * 返回用户的PlayerId
	 * 
	 * @return
	 */
	public abstract Long getPlayerId();

	private class CheckLoginTask implements Runnable {
		private Channel channel;

		public CheckLoginTask(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void run() {
			if (playerId == 0) {
				channel.close();
				System.out.println("由于连接建立后长时间未登陆");
			}
		}

	}

}
