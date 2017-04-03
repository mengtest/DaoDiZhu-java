package com.common.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 周期性任务定时器
 * @author wang guang shuai
 * @Date 2016年12月19日下午1:28:46
 */
public class TimerSignleTaskSchedule {

	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public static void submitTimerTask(Runnable task, int delay, int period, TimeUnit timeUnit) {
		executorService.scheduleAtFixedRate(task, delay, period, timeUnit);
	}

	public static void shutDown() {
		executorService.shutdown();
	}

	public static boolean isTerminated() {
		return executorService.isTerminated();
	}


}
