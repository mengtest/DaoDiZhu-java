package com.common.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.common.utils.ByteUtil;
import com.db.base.DBObject;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年11月2日 下午4:13:12
 */
public abstract class AbstractOneDao<T extends DBObject> extends AbstractDao {
	// 创建的默认缓存
	private final ConcurrentHashMap<Long, T> DB_OBJECT_CACHE = new ConcurrentHashMap<>(1000);

	/**
	 * 
	 * 描述：放入缓存一个数据对象
	 *
	 * @param id
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:24:31
	 *
	 */
	protected void addCache(long id, T dbObject) {
		DB_OBJECT_CACHE.put(id, dbObject);
	}

	/**
	 * 
	 * 描述：从缓存中获取一个数据对象
	 *
	 * @param id
	 * @param t
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:24:45
	 *
	 */
	protected T getFromCache(long id) {
		T t = DB_OBJECT_CACHE.get(id);
		if (t != null && !t.isDelete()) {
			return t;
		}
		return null;
	}

	protected void removeFromCache(Long id) {
		DB_OBJECT_CACHE.remove(id);
	}

	/**
	 * 
	 * 描述：保存到redis中。
	 *
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午2:56:28
	 *
	 */
	protected void saveToRedis(T dbObject) {
		byte[] key = dbObject.getRedisKey();
		byte[] value = ByteUtil.objToBytes(dbObject);
		redis.setValue(key, value, this.getRedisExpire());

	}

	/**
	 * 
	 * 描述：更新到redis
	 *
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:35:53
	 *
	 */
	protected void updateToRedis(T dbObject) {
		this.saveToRedis(dbObject);
	}

	/**
	 * 
	 * 描述：保存在缓存中
	 *
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午2:56:40
	 *
	 */
	protected void saveToCache(long id, T dbObject) {
		this.addCache(id, dbObject);
	}

	/**
	 * 
	 * 描述：保存一个数据对象，这个方法需要实现数据存储到redis和数据库中。是对外调用的方法
	 *
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午2:56:50
	 * @throws Exception
	 *
	 */
	public void save(T dbObject) throws Exception {
		dbObject.setDBInsertStatus();
		this.saveToCache(dbObject.getFirstId(), dbObject);
		redisTask.run(() -> {
			this.saveToRedis(dbObject);
		});

	}

	/**
	 * 
	 * 描述：更新一个数据对象。
	 *
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:04:56
	 * @throws Exception
	 *
	 */
	public void update(T dbObject) throws Exception {
		T old = this.selectOne(dbObject.getFirstId());
		if (old != dbObject) {
			throw new IllegalStateException("要更新对象与缓存对象地址不一致,可能是新new了一个对象");
		}
		dbObject.setDBUpdateStatus();
		redisTask.run(() -> {
			this.updateToRedis(dbObject);
		});
	}

	/**
	 * 
	 * 描述：删除一个对象
	 *
	 * @param id
	 * @param dbObject
	 * @throws Exception
	 * @author wang guang shuai
	 *
	 *         2016年11月18日 下午6:59:19
	 *
	 */
	public void delete(long id, T dbObject) throws Exception {
		T old = this.selectOne(id);
		if (old != dbObject) {
			throw new IllegalStateException("要更新对象与缓存对象地址不一致,可能是新new了一个对象");
		}
		dbObject.setDelete();
		this.DB_OBJECT_CACHE.remove(id);
		dbTask.run(() -> {
			this.deleteDB(dbObject);
		});
		redisTask.run(() -> {
			// 为一个对象打上已删除的标记
			this.saveToRedis(dbObject);
		});

	}

	/**
	 * 
	 * 描述：初始化数据，它先从redis中取数据，如果redis没有，再从数据库取，取出数据后存入缓存和redis中。
	 * 注意这个初始化的方法只能初始化一对一的操作，即一个id对应一条数据表中的一条记录,id必须是主键。在游戏中表现为可以通过一个roleId只能查询
	 * 到一条对应的记录。
	 * 
	 * @param ids
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:40:43
	 * @throws Exception
	 *
	 */
	public void initData(long id) throws Exception {
		// 已经初始化过了，不需要再初始化一次了。
		if (this.isInitSign(id)) {
			return;
		}
		// 标记已初始化过。
		this.setInitSign(id);
		// 先从redis中
		byte[] byteObj = redis.getValue(this.getRedisKey(id));
		T t = null;
		if (byteObj != null) {
			try {
				t = ByteUtil.byteToObj(byteObj, getDBObjectClass());
				if (t.isDelete()) {
					this.deleteRedis(t);
					return;
				}
				this.saveToCache(id, t);
				return;
			} catch (ClassNotFoundException | IOException e) {

			}
		}
		// 从数据库取
		t = mysqlClient.selectOne(this.getDBObjectClass(), this.getSelectSQL(id));
		if (t != null) {
			this.saveToRedis(t);
			this.saveToCache(id, t);
		}

	}

	/**
	 * 
	 * 描述：获取一个数据对象，它会先从缓存取，缓存如果没有，调用初始化方法，再重新从缓存取。
	 *
	 * @param id
	 * @param t
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月2日 下午3:51:09
	 * @throws Exception
	 *
	 */
	public T selectOne(Long id) throws Exception {
		T result = this.getFromCache(id);
		if (result == null) {
			this.initData(id);
			result = this.getFromCache(id);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.common.dao.AbstractDao#flushDB(long)
	 */
	@Override
	public void flushDB(long id) {

		T t = this.getFromCache(id);
		if (t != null) {
			dbTask.run(() -> {
				try {
					if (t.isDbInsertStatus()) {
						this.insertDB(t);
					} else if (t.isDbUpdateStatus()) {
						this.updateDB(t);
					}
					t.setEmptyDbStatus();
					this.saveToRedis(t);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	public void flushDBAndClearCache(long id) {
		this.flushDB(id);
		this.cleanInitSign(id);
		this.removeFromCache(id);
	}
	@Override
	public void flushDBAndClearCache(){
		Set<Long> keySet = DB_OBJECT_CACHE.keySet();
		for(Long id : keySet){
			this.flushDBAndClearCache(id);
		}
	}

	@Override
	public void timerFlushDb() {
		ConcurrentHashMap<Long, T> flushMap = this.DB_OBJECT_CACHE;
		Collection<T> col = flushMap.values();
		for (T t : col) {
			dbTask.run(() -> {
				flushDB(t.getFirstId());
			});
		}
	}

	/**
	 * 获取对象存储在redis中的key
	 * 
	 * @author youxijishu.com
	 * @param Id
	 * @return 2016年11月3日上午1:20:36
	 */
	protected abstract byte[] getRedisKey(long Id);

	/**
	 * 
	 * 描述：获取数据对象的class类型
	 *
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:45:50
	 *
	 */
	protected abstract Class<T> getDBObjectClass();

}
