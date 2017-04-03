package com.common.mysql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.common.utils.ReflectUtil;
import com.common.utils.StringUtil;

/**
 * 
 * @Desc 描述:Mysql操作的客户端类，此客户端对数据库的操作都没有主动关闭连接，理论上无限多个线程可以获得无限多个连接。
 *       所以在使用的时候要注意多线程的情况。
 * @author wang guang shuai
 * @Date 2016年9月16日 下午6:17:41
 */

public class MysqlClient {

	private volatile DruidDataSource dataSource = null;
	private Properties pro = null;

	public MysqlClient(Properties pro) {
		this.pro = pro;
		try {
			this.createDataSource();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DruidDataSource createDataSource() throws Exception {

		dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(pro);
		return dataSource;
	}

	public DruidPooledConnection getConnection() throws Exception {
		if (dataSource == null || dataSource.isClosed()) {
			synchronized (this) {
				if (dataSource == null || dataSource.isClosed()) {
					dataSource = this.createDataSource();
				}
			}
		}
		if (dataSource == null) {
			return null;
		}
		return dataSource.getConnection();

	}

	private void setPreparedStatement(PreparedStatement statement,String sql,Object...parameters) throws SQLException{
		int size = parameters.length;
		int parameterIndex = 0;
		for (int i = 0; i < size; i++) {
			parameterIndex = i + 1;
			statement.setObject(parameterIndex, parameters[i]);
		}
	}
	/**
	 * 
	 * @Desc 描述：执行sql的更新，包括update,insert,delete
	 * @param sql
	 * @author wang guang shuai
	 * @throws Exception
	 * @date 2016年9月16日 下午5:46:09
	 *
	 */
	public void executeUpdate(String sql,Object...parameters) throws Exception {
		DruidPooledConnection con = null;
		try {
			con = this.getConnection();
			if (con != null) {
				PreparedStatement ps = con.prepareStatement(sql);
				this.setPreparedStatement(ps, sql, parameters);
				ps.executeUpdate();
				con.close();
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	/**
	 * 
	 * @Desc 描述：查询一条记录
	 * @param sql
	 * @param t
	 * @param parameters
	 *            Sql语句中的参数
	 * @return
	 * @author wang guang shuai
	 * @throws Exception
	 * @date 2016年9月16日 下午7:31:54
	 *
	 */
	public <T> T selectOne(Class<T> t, String sql, Object... parameters) throws Exception {
		DruidPooledConnection con = this.getConnection();
		T result = null;
		if (con != null) {
			try {
				PreparedStatement stmt;
				stmt = con.prepareStatement(sql);
				this.setPreparedStatement(stmt, sql, parameters);
				ResultSet rs = stmt.executeQuery();
				// 如果查询的行数为0,则返回null;
				int row = this.getRowTotal(rs);
				if (row == 0) {
					return null;
				}
				if (row == 1) {
					rs.next();
					result = this.setResult(t, rs, rs.getMetaData().getColumnCount());

				} else {
					throw new SQLException("使用selectOne查询的行数大于一条记录");
				}
			} finally {
				if (con != null) {
					con.close();
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @Desc 描述：查询多条记录
	 * @param sql
	 * @param t
	 * @return
	 * @author wang guang shuai
	 * @throws Exception
	 * @date 2016年9月16日 下午7:31:43
	 *
	 */
	public <T> List<T> selectList(Class<T> t, String sql, Object... parameters) throws Exception {
		DruidPooledConnection con = this.getConnection();
		List<T> result = null;
		if (con != null) {
			try {
				PreparedStatement stmt;
				stmt = con.prepareStatement(sql);
				this.setPreparedStatement(stmt, sql, parameters);
				ResultSet rs = stmt.executeQuery();
				// 如果查询的行数为0,则返回null;
				int row = this.getRowTotal(rs);
				if (row == 0) {
					return null;
				}
				result = new ArrayList<>(rs.getRow());
				// 获取列数
				int colCount = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					T obj = this.setResult(t, rs, colCount);
					result.add(obj);
				}
			} finally {
				con.close();
			}
		}
		return result;
	}

	/**
	 * 
	 * 描述:这个方法是获取一共查询出了多少行
	 *
	 * @param resultSet
	 * @return 查询出的总行数
	 * @author Terry
	 * @throws SQLException
	 * @time 2016年6月15日-下午5:19:41
	 */
	private int getRowTotal(ResultSet resultSet) throws SQLException {
		resultSet.last();
		int rows = resultSet.getRow();
		resultSet.beforeFirst();
		return rows;

	}
	/**
	 * 
	 * @Desc 描述：通过反射给获得的对象赋值
	 * @param cl
	 * @param resultSet
	 * @param columnCount
	 *            列数
	 * @return
	 * @throws SQLException
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午6:32:19
	 *
	 */
	private <T> T setResult(Class<T> cl, ResultSet resultSet, int columnCount) throws SQLException {
		try {
			T t = (T) cl.newInstance();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int fieldType = 0;
			String columnName = null;
			String temp = null;
			Object value = null;
			String methodName = null;
			for (int i = 1; i <= columnCount; i++) {
				fieldType = metaData.getColumnType(i);
				columnName = metaData.getColumnName(i);
				temp = StringUtil.toCamelCase(columnName);
				value = resultSet.getObject(i);
				methodName = temp;
				switch (fieldType) {
				case Types.BOOLEAN:
				case Types.BIT:
					ReflectUtil.setValue(t, methodName, (Boolean) value, Boolean.class);
					break;
				case Types.TINYINT:
					ReflectUtil.setValue(t, methodName, ((Integer) value).byteValue(), Byte.class);
					break;
				case Types.SMALLINT:
					ReflectUtil.setValue(t, methodName, ((Integer) value).shortValue(), Short.class);
					break;
				case Types.INTEGER:
					ReflectUtil.setValue(t, methodName, (Integer) value, Integer.class);
					break;
				case Types.BIGINT:
					ReflectUtil.setValue(t, methodName, (Long) value, Long.class);
					break;
				
				case Types.FLOAT:
				case Types.REAL:
					ReflectUtil.setValue(t, methodName, (Float) value, Float.class);
					break;
				case Types.NUMERIC:
				case Types.DECIMAL:
					value = ((BigDecimal) value).doubleValue();
					break;
				case Types.DOUBLE:
					ReflectUtil.setValue(t, methodName, (Double) value, Double.class);
					break;
				case Types.VARCHAR:
				case Types.CHAR:
				case Types.LONGVARCHAR:
					ReflectUtil.setValue(t, methodName, (String) value, String.class);
					break;
				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
					ReflectUtil.setValue(t, methodName, (byte[]) value, byte[].class);
					break;
				case Types.DATE:
					ReflectUtil.setValue(t, methodName, (Date) value, Date.class);
					break;
				case Types.TIME:
					ReflectUtil.setValue(t, methodName, (Time) value, Time.class);
					break;
				case Types.TIMESTAMP:
					ReflectUtil.setValue(t, methodName, (Timestamp) value, Timestamp.class);
					break;
				default:
					throw new SQLException("数据库暂时不支持的类型：" + metaData.getColumnName(i));
				}
			}
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
