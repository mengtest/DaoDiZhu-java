package com.db.base;

import java.io.Serializable;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年12月7日 下午4:28:38
 */
@SuppressWarnings("serial")
public abstract class DBObject implements Serializable {

	public final static String ID = "id";

	private volatile ObjStatus dbStatus = null;

	private boolean isDelete = false;

	public void setDelete() {
		isDelete = true;
	}

	public boolean isDelete() {
		return isDelete;
	}

	/**
	 * 获取第一主键的id
	 * 
	 * @return the firstId
	 */
	public abstract long getFirstId();

	/**
	 * 获取第二主键的id
	 * 
	 * @return the secondId
	 */
	public abstract long getSecondId();

	/**
	 * 
	 * @Desc 描述：把需要写入数据库的字段展出为sql语句。
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午1:21:03
	 *
	 */
	public abstract String toUpdateSQL();

	public abstract Object[] toUpdateSQLParameters();

	/**
	 * 
	 * @Desc 描述：导出插入SQL语句
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午1:21:59
	 *
	 */
	public abstract String toInsertSQL();

	public abstract Object[] toInsertSQLParameters();

	/**
	 * 
	 * @Desc 描述：导出删除的sql语句
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午1:22:57
	 *
	 */
	public abstract String toDeleteSQL();

	/**
	 * 
	 * @Desc 描述：导出查询语句
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午1:26:21
	 *
	 */
	public abstract String toSelectSQL();

	/**
	 * 
	 * @Desc 描述：获取存储在redis对象的key,这个key一般对应的是redis中存储一个hashmap的key
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午8:39:40
	 *
	 */
	public abstract byte[] getRedisKey();

	/**
	 * @return the dbStatus
	 */
	public ObjStatus getDbStatus() {
		return dbStatus;
	}

	public void setDBInsertStatus() {
		dbStatus = ObjStatus.INSERT;
	}

	public void setDBUpdateStatus() {
		dbStatus = ObjStatus.UPDATE;
	}

	public void setDBDeleteStatus() {
		dbStatus = ObjStatus.DELETE;
	}

	public boolean isDbInsertStatus() {

		return dbStatus == ObjStatus.INSERT;
	}

	public boolean isDbUpdateStatus() {
		return dbStatus == ObjStatus.UPDATE;
	}

	public boolean isDbDeleteStatus() {
		return dbStatus == ObjStatus.DELETE;
	}

	public boolean isEmptyDbStatus() {
		return dbStatus == null;
	}

	public void setEmptyDbStatus() {
		dbStatus = null;
	}

	// 对象持久化的状态：1，插入，2，更新，3，删除
	enum ObjStatus {
		INSERT, UPDATE, DELETE
	}

}
