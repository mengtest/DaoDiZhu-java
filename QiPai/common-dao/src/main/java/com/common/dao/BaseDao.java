package com.common.dao;

import com.common.log.GameLogFactory;
import com.common.log.GameLogFactory.GameLogger;
import com.common.mysql.MysqlClient;
import com.common.mysql.MysqlFactory;
import com.common.redis.RedisClient;
import com.common.redis.RedisFactory;
import com.common.utils.AsyncTask;

public abstract class BaseDao {

	protected RedisClient redis = RedisFactory.getRedis();
	protected MysqlClient mysqlClient = MysqlFactory.getMysqlClient();
	protected GameLogger daoLog = GameLogFactory.newLogger("dao_log");
	public static AsyncTask redisTask = new AsyncTask(1);
	public static AsyncTask dbTask = new AsyncTask(1);
}
