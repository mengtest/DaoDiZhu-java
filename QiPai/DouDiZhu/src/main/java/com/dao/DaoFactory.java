package com.dao;

import java.util.concurrent.TimeUnit;

import com.common.dao.DaoTimerTask;
import com.network.config.ConfigFactory;
import com.network.config.DataTimerConfig;

public class DaoFactory {

	public static void registerDao(){
		DataTimerConfig dataTimerConfig = ConfigFactory.getGameServerConfig().getDataTimerConfig();
		DaoTimerTask.setTimerTime(dataTimerConfig.getDelay(), dataTimerConfig.getPeriod(), TimeUnit.SECONDS);
		DaoTimerTask.executeTimer();
	}
	
	public static void shuwdown(){
		DaoTimerTask.clearAllPlayer();
	}
}
