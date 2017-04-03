package com.common.utils;

import java.util.concurrent.TimeUnit;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年12月7日 下午5:24:25
 */
public class AsyncTask {

	private EventExecutorGroup loopGroup = null;

	public AsyncTask(int threadCounts) {
		loopGroup = new DefaultEventExecutorGroup(threadCounts);
	}

	/**
	 * 
	 * 描述：异步执行一些操作
	 *
	 * @param task
	 * @author wang guang shuai
	 *
	 *         2016年12月7日 下午5:26:20
	 *
	 */
	public void run(Runnable task) {

		EventExecutor loop = loopGroup.next();
		loop.execute(task);
	}

	public void run(Runnable task, int delay, TimeUnit timeUnit) {
		loopGroup.next().schedule(task, delay, timeUnit);
	}

	public void shutDown() {
		loopGroup.shutdownGracefully();
	}

	public void shutDownNow() {
		loopGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS);
	}

	public boolean isTerminated() {
		return loopGroup.isTerminated();
	}
}
