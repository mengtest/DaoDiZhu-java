package com.common.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.common.redis.RedisException;
import com.common.utils.ByteUtil;
import com.db.base.DBObject;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年11月2日 下午7:08:19
 */
public abstract class AbstractListDao<T extends DBObject> extends AbstractDao {

	private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, T>> DB_OBJECT_CACHE = new ConcurrentHashMap<>(1000);

	/**
	 * 
	 * 描述：向缓存中添加数据
	 *
	 * @param roleId
	 * @param id
	 * @param dbObject
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:32:40
	 *
	 */
	protected void addCache(long roleId, long id, T dbObject) {
		Map<Long, T> map = this.getMapValueFromCache(roleId);
		map.put(id, dbObject);
	}

	/**
	 * 
	 * 描述：从缓存中获取一个数据对象
	 *
	 * @param roleId
	 * @param id
	 * @param t
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:33:43
	 *
	 */
	protected T getFromCache(long roleId, long id) {
		Map<Long, T> map = this.getMapValueFromCache(roleId);
		return (T) map.get((long) id);
	}

	protected void removeFromCache(long roleId) {
		DB_OBJECT_CACHE.remove(roleId);
	}

	/**
	 * 
	 * 描述：存储到缓存中
	 *
	 * @param roleId
	 * @param id
	 * @param t
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:55:33
	 *
	 */
	protected void saveToCache(long roleId, long id, T t) {
		this.addCache(roleId, id, t);
	}

	/**
	 * 
	 * 描述：存储到redis
	 *
	 * @param t
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:57:04
	 *
	 */
	protected void saveToRedis(T t) {
		byte[] key = t.getRedisKey();
		Long id = t.getSecondId();
		byte[] field = String.valueOf(id).getBytes();
		byte[] value = ByteUtil.objToBytes(t);
		redis.setMapValue(key, field, value, this.getRedisExpire());
	}

	/**
	 * 
	 * 描述：更新数据到redis中。
	 *
	 * @param t
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午11:03:32
	 *
	 */
	protected void updateToRedis(T t) {
		this.saveToRedis(t);
	}

	/**
	 * 
	 * 描述：保存插入数据，数据会先存到内存缓存，再存到redis，再存储到mysql
	 *
	 * @param roleId
	 * @param id
	 *            数据对象的二级索引id，比如存储一个物品对象，这里就是物品id
	 * @param t
	 *            数据对象
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午11:04:19
	 * @throws Exception
	 *
	 */
	public void save(long roleId, long id, T t) throws Exception {
		this.saveToCache(roleId, id, t);
		t.setDBInsertStatus();
		redisTask.run(() -> {
			this.saveToRedis(t);
		});

	}

	/**
	 * 
	 * 描述：更新数据，它会先更新到redis，再更新到mysql,内存缓存由使用者在逻辑中更新，因为这里缓存的对象就是这里传来的参数T t
	 *
	 * @param roleId
	 * @param t
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午11:07:00
	 * @throws Exception
	 *
	 */
	public void update(long roleId, T t) throws Exception {
		T old = this.selectOne(roleId, t.getSecondId());
		if (old != t) {
			throw new IllegalStateException("要更新对象与缓存对象地址不一致,可能是新new了一个对象," + t + "," + old + "," + t.getSecondId());
		}
		t.setDBUpdateStatus();
		redisTask.run(() -> {
			this.updateToRedis(t);
		});

	}

	/**
	 * 
	 * 描述：删除一个对象
	 *
	 * @param Id
	 * @param t
	 * @throws RedisException
	 * @author wang guang shuai
	 *
	 *         2016年11月18日 下午7:03:16
	 *
	 */
	public void delete(long Id, T t) throws RedisException {

		Map<Long, T> map = this.getMapValueFromCache(Id);
		if (map != null) {
			map.remove(t.getSecondId());
		}
		t.setDelete();
		dbTask.run(() -> {
			this.deleteDB(t);
		});
		redisTask.run(() -> {
			this.updateToRedis(t);
		});

	}

	/**
	 * 
	 * 描述：查询一个数据对象，它会先从内存查询，内存没有查redis，redis没有查数据库
	 *
	 * @param roleId
	 * @param Id
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午11:08:05
	 * @throws Exception
	 *
	 */
	public T selectOne(long roleId, long Id) throws Exception {
		T t = this.getFromCache(roleId, Id);
		if (t == null) {
			this.initData(roleId);
			t = this.getFromCache(roleId, Id);
		}
		return t;
	}

	/**
	 * 
	 * 描述：查询当前所有的数据对象集合，先从缓存查，如果没有再从redis查，如果还没有再从数据库查
	 *
	 * @param roleId
	 * @return
	 * @throws Exception
	 * @author wang guang shuai
	 *
	 *         2016年11月4日 下午1:32:30
	 *
	 */
	public Collection<T> selectAllOfList(long roleId) throws Exception {
		Map<Long, T> map = this.selectAllOfMap(roleId);
		return map.values();
	}

	public Map<Long, T> selectAllOfMap(long roleId) throws Exception {
		Map<Long, T> map = this.getMapValueFromCache(roleId);
		if (map.isEmpty()) {
			this.initData(roleId);
			map = this.getMapValueFromCache(roleId);
		}
		return map;
	}

	protected abstract byte[] getRedisMapKey(long id);

	/**
	 * 
	 * 描述：如果内存缓存不存在，则先从redis初始化数据，如果redis中也没有，再从数据库中初始化数据。
	 *
	 * @param roleId
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午11:10:42
	 * @throws Exception
	 *
	 */
	protected void initData(long roleId) throws Exception {
		if (this.isInitSign(roleId)) {
			return;
		}
		this.setInitSign(roleId);
		byte[] key = this.getRedisMapKey(roleId);
		Map<byte[], byte[]> selectResult = redis.getAllMapValue(key);
		if (selectResult != null) {
			Collection<byte[]> col = selectResult.values();
			try {
				for (byte[] entry : col) {
					if (entry != null) {
						T t = ByteUtil.byteToObj(entry, getDBObjectClass());
						this.saveToCache(roleId, t.getSecondId(), t);
					}
				}
				return;
			} catch (ClassNotFoundException | IOException e) {
				//e.printStackTrace();
			}
		}
		List<T> resultList = mysqlClient.selectList(this.getDBObjectClass(), this.getSelectSQL(roleId));
		if (resultList != null) {
			for (T t : resultList) {
				this.saveToCache(t.getFirstId(), t.getSecondId(), t);
				this.saveToRedis(t);
			}
		}

	}

	/**
	 * 
	 * 描述：从缓存中获取一个玩家数据的集合map,如果这个map不存在，会自动创建一个空的map并放入缓存中。
	 *
	 * @param roleId
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:31:09
	 *
	 */
	public ConcurrentHashMap<Long, T> getMapValueFromCache(long roleId) {
		ConcurrentHashMap<Long, T> map = DB_OBJECT_CACHE.get(roleId);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			DB_OBJECT_CACHE.put(roleId, map);
		}
		return map;
	}

	@Override
	public void timerFlushDb() {
		Collection<Long> col = this.DB_OBJECT_CACHE.keySet();
		for (Long id : col) {
			this.flushDB(id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.common.dao.AbstractDao#flushDB(long)
	 */
	@Override
	public void flushDB(long id) {
		ConcurrentHashMap<Long, T> map = this.getMapValueFromCache(id);
		Collection<T> col = map.values();
		for (T t : col) {
			if (t != null) {
				dbTask.run(() -> {
					try {
						if (t.isDbInsertStatus()) {
							this.insertDB(t);
						} else if (t.isDbUpdateStatus()) {
							this.updateDB(t);
						}
						t.setDBUpdateStatus();
						this.saveToRedis(t);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

	@Override
	public void flushDBAndClearCache(long id) {
		this.removeFromCache(id);
		this.flushDB(id);
		this.cleanInitSign(id);

	}
	public void flushDBAndClearCache(){
		Set<Long> keySet = DB_OBJECT_CACHE.keySet();
		for(Long id : keySet){
			this.flushDBAndClearCache(id);
		}
	}

	/**
	 * 
	 * 描述：获取数据对象的class
	 *
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 上午10:49:09
	 *
	 */
	protected abstract Class<T> getDBObjectClass();

}
