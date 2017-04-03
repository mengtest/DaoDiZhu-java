package com.dao;

import com.common.redis.IRedisExpire;
import com.common.utils.DateUtil;

public enum RedisExpire implements IRedisExpire {
	DEFAUT_EXPIRE(DateUtil.DAY_15),
	ROOM_EXPIRE(60 * 60);
	private int time;

	private RedisExpire(int time) {
		this.time = time;
	}

	@Override
	public int getExpire() {
		return time;
	}

}
