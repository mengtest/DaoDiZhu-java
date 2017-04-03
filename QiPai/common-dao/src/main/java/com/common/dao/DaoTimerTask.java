package com.common.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.common.log.GameLogFactory;
import com.common.log.GameLogFactory.GameLogger;
import com.common.timer.TimerSignleTaskSchedule;
/**
 * 数据定时同步任务,同步数据到数据库的定时器。默认为10分钟同步一次，可以通过setTimerTime方法修改，但必须在executeTimer之前调用。
 * @author wang guang shuai
 * @Date  2017年1月11日下午3:25:43
 */
public class DaoTimerTask {

	private static List<AbstractDao> daoList = new ArrayList<>();
	private static int _delayTime = 600;
	private static int _period = 600;
	private static TimeUnit _timeUnit = TimeUnit.SECONDS;
	private static GameLogger gameLogger = GameLogFactory.newLogger(TimerSignleTaskSchedule.class);
	/**
	 * 
	 * 描述:设置
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月19日下午1:25:44
	 * @param delayTime
	 * @param period
	 */
	public static void setTimerTime(int delayTime, int period, TimeUnit timeUnit) {
		_delayTime = delayTime;
		_period = period;
		_timeUnit = timeUnit;
	}

	/**
	 * 注册一个dao的实现类。
	 * 
	 * @param dao
	 */
	public static void registerDao(AbstractDao dao) {
		daoList.add(dao);
	}

	/**
	 * 
	 * 描述:清理并存储一个玩家的所有数据。一般是在下线后长时间未登陆或长时间未操作的情况下进行。
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月19日上午11:28:22
	 * @param playerId
	 */
	public static void clearOnePlayer(long playerId) {
		for (AbstractDao dao : daoList) {
			dao.flushDBAndClearCache(playerId);
		}
	}

	/**
	 * 
	 * 描述:把一个集合中的用户同步到数据库中。一般用于关闭服务器时，把所有的玩家数据写回数据库。
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月27日下午4:10:22
	 * @param allPlayer
	 */
	public static void clearAllPlayer() {
		for (AbstractDao dao : daoList) {
			System.out.println("正在同步数据：" + dao.getClass().getName());
			dao.flushDBAndClearCache();
		}
		AbstractDao.redisTask.shutDown();
		AbstractDao.dbTask.shutDown();
		int time = 0;
		while (true) {
			if (AbstractDao.redisTask.isTerminated() && AbstractDao.dbTask.isTerminated()) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time++;
			System.out.println("正在停止数据同步线程池，用时 " + time + " 秒");
		}
		System.out.println("--------------同步数据完成-------------");
	}

	/**
	 * 开启定时执行任务，每隔十分钟同步一次数据库
	 */
	public static void executeTimer() {
		TimerSignleTaskSchedule.submitTimerTask(new Task(), _delayTime, _period, _timeUnit);
	}

	private static class Task implements Runnable {

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			for (AbstractDao dao : daoList) {
				try {
					dao.timerFlushDb();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			long end = System.currentTimeMillis();
			//gameLogger.debug(0, "执行同步结束，用时：" + (end - start) + "ms");
		}

	}
}
