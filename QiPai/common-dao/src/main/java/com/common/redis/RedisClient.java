package com.common.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisClient {
	private volatile JedisPool localPool = null;
	private RedisConfig localRedis;

	public RedisClient(RedisConfig redisConfig) {
		this.localRedis = redisConfig;
		this.init();
	}

	public RedisConfig getRedisConfig() {
		return localRedis;
	}

	private void init() {

		JedisPoolConfig config1 = new JedisPoolConfig();
		
		config1.setBlockWhenExhausted(true);
		config1.setMaxIdle(localRedis.getMaxIdle());
		config1.setMaxTotal(localRedis.getMaxTotal());
		config1.setMaxWaitMillis(localRedis.getMaxWaitMillis());
		config1.setMinEvictableIdleTimeMillis(localRedis.getMinEvictableIdleTimeMillis());
		config1.setMinIdle(localRedis.getMinIdle());
		config1.setTimeBetweenEvictionRunsMillis(localRedis.getTimeBetweenEvictionRunsMillis());
		// 如果为true，表示有一个idle object evitor线程对idle
		// object进行扫描，如果validate失败，此object会被从pool中drop掉；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		config1.setTestWhileIdle(true);
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的
		config1.setTestOnBorrow(true);
		// 在return给pool时，是否提前进行validate操作
		config1.setTestOnReturn(true);
		if (localRedis.getPassword() != null && !localRedis.getPassword().isEmpty()) {
			localPool = new JedisPool(config1, localRedis.getIp(), localRedis.getPort(), localRedis.getTimeout(),
					localRedis.getPassword());
		} else {
			localPool = new JedisPool(config1, localRedis.getIp(), localRedis.getPort());
		}
	}

	public void clearRedis() {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();

			jedis.flushAll();
			jedis.flushDB();
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：获取当前活跃的redis数量
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午7:19:27
	 *
	 */
	public int getNowRedisCount() {
		return localPool.getNumActive();
	}

	/**
	 * 
	 * @Desc 描述：获取一个redis的连接客户端
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午6:54:06
	 *
	 */
	public Jedis getRedisClient() {
		if (localPool != null) {
			try {
				if (localPool.isClosed()) {
					synchronized (this) {
						if (localPool.isClosed()) {
							this.init();
						}
					}
				}
				return localPool.getResource();
			} catch (Exception e) {
				RedisExceptionFilter.getInstance().exceptionCatch(e, "获取redis客户端连接失败");
			}
		}
		return null;
	}

	private void close(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	/**
	 * 
	 * 描述:发布一条消息。
	 * @author wang guang shuai
	 * @Date   2016年12月21日下午2:14:03
	 * @param key
	 * @param value
	 */
	public void publish(String key,String value){
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			jedis.publish(key, value);
		} finally {
			this.close(jedis);
		}
	}
	/**
	 * 
	 * 描述:订阅总个频道。
	 * @author wang guang shuai
	 * @Date   2016年12月21日下午2:20:16
	 * @param channel
	 * @param pubSub
	 */
	public void subscribe(String channel,JedisPubSub pubSub){
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			jedis.subscribe(pubSub, channel);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：删除redis中的一个key
	 * @param key
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:54:31
	 *
	 */
	public void deleteKey(String key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();

			jedis.del(key);
		} finally {
			this.close(jedis);
		}
	}
	public void deleteKey(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();

			jedis.del(key);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：设置一个key-value的值
	 * @param key
	 * @param value
	 * @param expire
	 *            值的过期时间,如果为null，不设置
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:54:42
	 *
	 */
	public void setValue(String key, String value, IRedisExpire expire) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			if (expire != null) {
				jedis.setex(key, expire.getExpire(), value);
			} else {
				jedis.set(key, value);
			}
		} finally {
			this.close(jedis);
		}
	}

	public void setValue(byte[] key, byte[] value, IRedisExpire expire) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			if (expire != null) {
				jedis.setex(key, expire.getExpire(), value);
			} else {
				jedis.set(key, value);
			}
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：判断redis中是否存在某个key
	 * @param key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月26日 下午6:25:46
	 *
	 */
	public boolean isExistKey(String key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			return jedis.exists(key);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：从redis中原子增加1，默认从0开始
	 * @param key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月26日 下午7:28:51
	 *
	 */
	public long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			return jedis.incr(key);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：redis原子增加一个值，增加的量是incrValue;
	 * @param key
	 * @param incrValue
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月26日 下午7:29:16
	 *
	 */
	public long incrBy(String key, long incrValue) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			return jedis.incrBy(key, incrValue);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：批量获取多个redis的Map集合，需要注意的是，如果keys中的某个key对应的map，不存在，list中将返回一个map
	 *       size为0的map。
	 * @param keys
	 * @return
	 * @author wang guang shuai
	 * @date 2016年10月12日 下午3:32:37
	 *
	 */
	public List<Map<String, String>> getMapByPipeline(String... keys) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			Pipeline pipeline = jedis.pipelined();
			Map<String, Response<Map<String, String>>> responseMap = new HashMap<>();
			for (String key : keys) {
				responseMap.put(key, pipeline.hgetAll(key));
			}
			pipeline.sync();
			List<Map<String, String>> resultList = new ArrayList<>();
			for (Map.Entry<String, Response<Map<String, String>>> entry : responseMap.entrySet()) {
				Response<Map<String, String>> response = entry.getValue();
				if (response != null && response.get() != null && response.get().size() > 0) {
					resultList.add(response.get());
				}
			}
			if (resultList.size() > 0) {
				return resultList;
			}
			return null;
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：获取一个key的value值
	 * @param key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:54:54
	 *
	 */
	public String getValue(String key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			return jedis.get(key);
		} finally {
			this.close(jedis);
		}
	}

	public byte[] getValue(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			return jedis.get(key);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：向redis存储一个map的值
	 * @param key
	 * @param map
	 * @param expire
	 *            过期时间，如果为null,永久有效
	 * @author wang guang shuai
	 * @date 2016年9月23日 下午6:09:20
	 *
	 */
	public void setMapValue(String key, Map<String, String> map, IRedisExpire expire) {
		if (map != null && map.size() > 0) {
			Jedis jedis = null;
			try {
				jedis = this.getRedisClient();
				jedis.hmset(key, map);
				if (expire != null) {
					jedis.expire(key, expire.getExpire());
				}
			} finally {
				this.close(jedis);
			}
		}
	}

	/**
	 * 
	 * @Desc 描述：向redis的map中插入一条记录
	 * @param key
	 * @param field
	 * @param value
	 * @param expire
	 * @author wang guang shuai
	 * @date 2016年10月8日 下午5:28:05
	 *
	 */
	public void setMapValue(String key, String field, String value, IRedisExpire expire) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			jedis.hset(key, field, value);
			if (expire != null) {
				jedis.expire(key, expire.getExpire());
			}
		} finally {
			this.close(jedis);
		}
	}

	public void setMapValue(byte[] key, byte[] field, byte[] value, IRedisExpire expire) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			jedis.hset(key, field, value);
			if (expire != null) {
				jedis.expire(key, expire.getExpire());
			}
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：获取redis中map存储的所有值，注意，这个方法不能用于存储太多数据的map，要不然会影响性能
	 * @param key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年10月8日 下午5:49:26
	 *
	 */
	public Map<String, String> getAllMapValue(String key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			Map<String, String> map = jedis.hgetAll(key);
			return map;
		} finally {
			this.close(jedis);
		}
	}
	public Map<byte[], byte[]> getAllMapValue(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			Map<byte[], byte[]> map = jedis.hgetAll(key);
			return map;
		} finally {
			this.close(jedis);
		}
	}

	public byte[] getMapValue(byte[] key, byte[] field) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			byte[] result = jedis.hget(key, field);
			return result;
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：
	 * @param key
	 * @param value
	 * @param expire
	 * @param left
	 *            true加入到list的左则，false，加入到redis的右则
	 * @author wang guang shuai
	 * @date 2016年9月23日 下午6:24:45
	 *
	 */
	public void addValueForList(String key, boolean left, IRedisExpire expire, String... values) {
		Jedis jedis = null;
		try {
			jedis = this.getRedisClient();
			if (left) {
				jedis.lpush(key, values);
			} else {
				jedis.rpush(key, values);
			}
			if (expire != null) {
				jedis.expire(key, expire.getExpire());
			}
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：从一个redis的list列表中pop出一个值
	 * @param key
	 * @param left
	 *            true 从list左则取出，false从list的右则取出。
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月23日 下午6:30:19
	 *
	 */
	public String popFromList(String key, boolean left) {
		Jedis jedis = null;
		String value = null;
		try {

			jedis = this.getRedisClient();
			if (left) {
				value = jedis.lpop(key);
			} else {
				value = jedis.rpop(key);
			}
		} finally {
			this.close(jedis);
		}
		return value;
	}

	/**
	 * 
	 * @Desc 描述：向redis的set集合中添加成员值
	 * @param key
	 * @param members
	 * @author wang guang shuai
	 * @date 2016年10月12日 上午10:39:25
	 *
	 */
	public void addValueForSet(String key, String... members) {
		Jedis jedis = null;
		try {

			jedis = this.getRedisClient();
			jedis.sadd(key, members);
		} finally {
			this.close(jedis);
		}
	}

	/**
	 * 
	 * @Desc 描述：判断某个成员变量是否在这个key的集合中
	 * @param key
	 * @param member
	 * @return
	 * @author wang guang shuai
	 * @date 2016年10月12日 上午10:43:13
	 *
	 */
	public boolean existInSet(String key, String member) {
		Jedis jedis = null;
		try {

			jedis = this.getRedisClient();
			return jedis.sismember(key, member);
		} finally {
			this.close(jedis);
		}
	}

}
