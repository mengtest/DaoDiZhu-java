package com.common.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.common.redis.IRedisExpire;
import com.common.redis.RedisException;
import com.common.utils.JsonUtil;
import com.db.base.DBObject;

/**
 * @Desc 描述:
 * @author wang guang shuai
 * @Date 2016年10月8日 下午1:00:38
 */
public abstract class AbstractDao extends BaseDao{
	
	// 记录是否初始化过数据，如果已初始化过，就不会再去redis和数据库请求数据了。防止缓存穿透
	private final ConcurrentHashMap<Long, Boolean> INIT_SIGN_CACHE = new ConcurrentHashMap<>();

	public void setInitSign(Long id) {
		INIT_SIGN_CACHE.put(id, true);
	}

	/**
	 * 
	 * 描述：判断是否已初始化过数据，返回true表示已初始化过。
	 *
	 * @param id
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年12月9日 下午7:00:41
	 *
	 */
	public boolean isInitSign(long id) {
		return INIT_SIGN_CACHE.get(id) != null;
	}

	public void cleanInitSign(long id) {
		INIT_SIGN_CACHE.remove(id);
	}

	public <T> List<T> selectAllFromDB(Class<T> t, String sql) throws Exception {
		List<T> resultList = mysqlClient.selectList(t, sql);
		return resultList;
	}

	/**
	 * 
	 * 描述：把数据更新到数据库，从内存中取出数据，并写入到数据库。
	 *
	 * @param id
	 * @author wang guang shuai
	 *
	 *         2016年12月9日 下午4:29:27
	 *
	 */
	public abstract void flushDB(long id);

	/**
	 * 
	 * 描述：把数据更新到数据库并清空内存
	 *
	 * @param id
	 * @author wang guang shuai
	 *
	 *         2016年12月9日 下午5:36:55
	 *
	 */
	public abstract void flushDBAndClearCache(long id);

	/**
	 * 
	 * 描述:把有的人的数据更新到数据库
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月27日下午4:14:04
	 */
	public abstract void flushDBAndClearCache();

	/**
	 * 
	 * 描述:定时更新数据到数据库中。
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月12日下午1:17:29
	 */
	public abstract void timerFlushDb();

	/**
	 * 
	 * @Desc 描述：更新数据库
	 * @param id
	 * @param dbObject
	 * @author wang guang shuai
	 * @throws Exception
	 * @throws RedisException
	 * @date 2016年10月11日 下午2:26:22
	 *
	 */
	protected void updateDB(DBObject dbObject) {
		String sql = dbObject.toUpdateSQL();
		try {
			mysqlClient.executeUpdate(sql, dbObject.toUpdateSQLParameters());
		} catch (Exception e) {
			this.sqlFailedLog(sql, dbObject, e);
		}
	}

	/**
	 * 
	 * @Desc 描述：插入数据库
	 * @param id
	 * @param dbObject
	 * @author wang guang shuai
	 * @throws Exception
	 * @throws RedisException
	 * @date 2016年10月11日 下午2:26:32
	 *
	 */
	protected void insertDB(DBObject dbObject) {
		String sql = dbObject.toInsertSQL();
		try {
			mysqlClient.executeUpdate(sql, dbObject.toInsertSQLParameters());
		} catch (Exception e) {
			this.sqlFailedLog(sql, dbObject, e);
		}
	}

	/**
	 * 
	 * 描述：从数据库中删除一个对象
	 *
	 * @param id
	 * @param dbObject
	 * @throws RedisException
	 * @author wang guang shuai
	 *
	 *         2016年11月18日 下午6:58:22
	 * @throws Exception
	 *
	 */
	protected void deleteDB(DBObject dbObject) {
		String sql = dbObject.toDeleteSQL();
		try {
			mysqlClient.executeUpdate(sql);
		} catch (Exception e) {
			this.sqlFailedLog(sql, dbObject, e);
		}
	}

	/**
	 * 
	 * 描述：从redis中删除某个对象
	 *
	 * @param id
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月18日 下午6:58:06
	 *
	 */
	protected void deleteRedis(DBObject dbObject) {
		redis.deleteKey(dbObject.getRedisKey());
	}

	/**
	 * 
	 * 描述:记录操作失败的数据。
	 * 
	 * @author wang guang shuai
	 * @Date 2017年1月11日下午2:24:32
	 * @param t
	 */
	public void sqlFailedLog(String sql, DBObject t, Throwable e) {
		daoLog.error(t.getFirstId(), e, JsonUtil.objToJson(t), "\n", sql);
	}

	public void redisFailedLog(DBObject t, Throwable e) {
		daoLog.error(t.getFirstId(), e, JsonUtil.objToJson(t));
	}

	/**
	 * 
	 * 描述：返回一个查询的SQL。这个需要你根据业务的需要，返回查询多个或单个的sql语句。
	 *
	 * @param roleId
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午4:07:45
	 *
	 */
	protected abstract String getSelectSQL(long roleId);

	/**
	 * 
	 * 描述:获取在redis中缓存的有效时间。
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月12日下午4:37:50
	 * @return
	 */
	protected abstract IRedisExpire getRedisExpire();

}
