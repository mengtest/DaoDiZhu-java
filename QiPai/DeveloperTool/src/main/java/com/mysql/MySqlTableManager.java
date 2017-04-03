package com.mysql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.utils.SQLUtil;
import com.common.utils.StringUtil;
import com.config.ToolConfig;

/**
 * @Desc 描述:管理mysql的表，生成相对应的类
 * @author wang guang shuai
 * @Date 2016年9月21日 下午6:53:18
 */
public class MySqlTableManager {

	private final static String oneTab = "\n\t";
	private final static String twoTab = "\n\t\t";
	private final static String threeTab = "\n\t\t\t";
	private final static String fourTab = "\n\t\t\t\t";

	/**
	 * 
	 * @Desc 描述：创建一个数据库连接
	 * @param mySqlConfig
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月16日 下午4:49:33
	 *
	 */
	public Connection createConnection() {
		Connection conn = null;
		try {
			ToolConfig config = ToolConfig.getInstance();
			Class.forName(config.getJBDCDriver());
			conn = DriverManager.getConnection(config.getMysqlUrl(), config.getMysqlUserName(),
					config.getMysqlPassword());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 
	 * @Desc 描述：获取所有表的名字
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午7:24:31
	 *
	 */
	public List<String> getAllTableNames() {
		List<String> list = new ArrayList<>();
		Connection connection = null;
		try {
			connection = this.createConnection();
			DatabaseMetaData dmd = (DatabaseMetaData) connection.getMetaData();
			ResultSet rs = dmd.getTables(null, null, "%", null);
			while (rs.next()) {
				list.add(rs.getString("TABLE_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public void tableToObject() {
		Connection conn = this.createConnection();
		List<String> allTableNames = this.getAllTableNames();
		try {
			Statement statement = conn.createStatement();
			int index = 0;
			for (String tableName : allTableNames) {
				index++;
				StringBuilder javaClass = new StringBuilder();
				ResultSet rs = statement.executeQuery("select * from " + tableName);
				rs.next();
				this.importClass(javaClass);
				this.createClass(javaClass, tableName);
				javaClass.append("{").append(oneTab);
				javaClass.append("private static final long serialVersionUID = ").append(index).append(";")
						.append(oneTab);
				javaClass.append("public static final String tableName = \"").append(tableName).append("\";")
						.append(oneTab);

				this.createField(javaClass, rs, tableName);
				this.createSetGeter(javaClass, rs, tableName);
				this.createGetRedisKeyMethod(javaClass, tableName, rs);
				this.createToUpdateSQLMethod(rs, javaClass, tableName);
				this.createToInsertSQLMethod(javaClass, rs, tableName);
				this.createToDeleteSQL(javaClass, rs, tableName);
				this.createToSelectSQL(javaClass, rs, tableName);
				javaClass.append("\n").append("}");
				String classPath = ToolConfig.getInstance().getMysqlTableCodePath() + File.separator
						+ this.getClassName(tableName) + ".java";
				File file = new File(classPath);
				OutputStream outputStream = new FileOutputStream(file);
				outputStream.write(javaClass.toString().getBytes());
				outputStream.close();
				System.out.println("处理数据库表[" + tableName + "]成功");
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Desc 描述：导入引用的类
	 * @param buffer
	 * @author wang guang shuai
	 * @date 2016年9月21日 下午7:35:21
	 *
	 */
	private void importClass(StringBuilder buffer) {
		buffer.append("package com.db.model;\n").append("import com.db.base.DBObject;\n")
				.append("import com.common.utils.StringUtil;\n");
	}

	/**
	 * 
	 * @Desc 描述：创建类名字和继承
	 * @param buffer
	 * @param tableName
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:40:59
	 *
	 */
	private void createClass(StringBuilder buffer, String tableName) {
		buffer.append("public class ").append(this.getClassName(tableName)).append(" extends DBObject ");
	}

	/**
	 * 
	 * @Desc 描述：创建类的字段信息
	 * @param javaClass
	 * @param rs
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:44:04
	 *
	 */
	private void createField(StringBuilder javaClass, ResultSet rs, String tableName) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		String columnName = null;
		// 生成静态字段
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);
			javaClass.append(oneTab).append("public final static String ").append(columnName.toUpperCase())
					.append(" = ").append("\"").append(columnName).append("\";");

		}
		// 获取各个数据库字段的注释
		Map<String, String> remarksMap = new HashMap<>();
		Connection con = this.createConnection();
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet resultSet = dbmd.getColumns(null, "%", tableName, "%");
		while (resultSet.next()) {
			String colnumname = resultSet.getString("COLUMN_NAME");
			String remarks = resultSet.getString("REMARKS");

			if (remarks == null || remarks.isEmpty()) {
				remarks = colnumname;
			}
			remarksMap.put(colnumname, remarks);
		}

		// 生成成员变量字段
		// for (int i = 0; i < columnCount; i++) {
		// columnName = metaData.getColumnName(i + 1);
		resultSet = dbmd.getColumns(null, "%", tableName, "%");
		while (resultSet.next()) {
			columnName = resultSet.getString("COLUMN_NAME");

			int date_type = resultSet.getInt("DATA_TYPE");
			String type = SQLUtil.getJavaType(date_type);
			javaClass.append(oneTab).append("//").append(remarksMap.get(columnName)).append(oneTab).append("private ")
					.append(type).append(" ").append(this.getFieldName(columnName)).append("");

			// 默认值
			String default_value = resultSet.getString("COLUMN_DEF");
			if (type.equals("String")) {
				javaClass.append(" = \"\"");
			} else if (type.equals("Boolean")) {
				javaClass.append(" = false");
			} else if (type.equals("Integer")) {
				javaClass.append(" = ").append(StringUtil.valueOfInt(default_value));
			} else if (type.equals("Long")) {
				javaClass.append(" = ").append(StringUtil.valueOfLong(default_value)).append("L");
			} else if (type.equals("Byte")) {
				javaClass.append(" = ").append(StringUtil.valueOfByte(default_value));
			}
			javaClass.append(";");
		}
		con.close();
		// }
	}

	/**
	 * 
	 * @Desc 描述：创建字段的get set 方法
	 * @param javaClass
	 * @param rs
	 * @author wang guang shuai
	 * @throws SQLException
	 * @date 2016年9月22日 上午10:12:03
	 *
	 */
	private void createSetGeter(StringBuilder javaClass, ResultSet rs, String tableName) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		String columnName = null;
		List<String> primaryKeys = this.getPrimaryKey(tableName);

		javaClass.append(oneTab);
		String dataId1 = "0L";
		String dataId2 = "0L";
		if (primaryKeys.size() == 1) {
			dataId1 = this.getFieldName(metaData.getColumnName(1));
		}
		if (primaryKeys.size() == 2) {
			dataId2 = this.getFieldName(metaData.getColumnName(2));
		}
		javaClass.append("public long getFirstId(){").append(twoTab).append("return ").append(dataId1).append(";")
				.append(oneTab).append("}").append(oneTab);
		javaClass.append("public long getSecondId(){").append(twoTab).append("return ").append(dataId2).append(";")
				.append(oneTab).append("}");

		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);

			javaClass.append(oneTab);
			String type = SQLUtil.getJavaType(metaData.getColumnType(i + 1));

			// get方法
			javaClass.append("public ").append(type).append(" ").append("get").append(this.getClassName(columnName))
					.append("() {").append(twoTab).append("return ").append(this.getFieldName(columnName)).append(";")
					.append(oneTab).append("}");

			// set方法
			javaClass.append(oneTab);

			javaClass.append("public void set").append(this.getClassName(columnName)).append("(").append(type)
					.append(" ").append(this.getFieldName(columnName)).append(") {").append(twoTab).append("this.")
					.append(this.getFieldName(columnName)).append(" = ").append(this.getFieldName(columnName))
					.append(";");

			javaClass.append(oneTab).append("}");

		}
	}

	/**
	 * 
	 * @Desc 描述：创建GetRedisKey方法
	 * @param javaClass
	 * @param tableName
	 * @param rs
	 * @author wang guang shuai
	 * @throws SQLException
	 * @date 2016年9月22日 上午11:19:49
	 *
	 */
	private void createGetRedisKeyMethod(StringBuilder javaClass, String tableName, ResultSet rs) throws SQLException {
		List<String> primaryKeys = this.getPrimaryKey(tableName);
		if (primaryKeys.isEmpty()) {
			throw new NullPointerException("数据库表：" + tableName + "不存在主键，请定义主键");
		}
		javaClass.append(oneTab);
		javaClass.append("@Override").append(oneTab);

		javaClass.append("public byte[] getRedisKey() {").append(twoTab);
		javaClass.append("return getRedisKey(");
		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String colName = metaData.getColumnName(i);
			if (colName.toLowerCase().endsWith("id")) {
				javaClass.append("this.get").append(this.getClassName(colName)).append("(),");
				break;
			}
		}
		javaClass.delete(javaClass.length() - 1, javaClass.length());
		javaClass.append(");").append(oneTab).append("}");

		javaClass.append(oneTab);
		javaClass.append("public static  byte[] getRedisKey(long id)");
		javaClass.append("{").append(twoTab);
		javaClass.append("StringBuilder key = new StringBuilder();").append(twoTab).append("key.append(tableName)");
		javaClass.append(".append(StringUtil.COLON).append(").append("id").append(")");
		javaClass.append(";").append(twoTab).append("return key.toString().getBytes();").append(oneTab).append("}");
		;

	}

	

	

	
	private void createToUpdateSQLMethod(ResultSet rs, StringBuilder javaClass, String tableName) throws SQLException {
		javaClass.append(oneTab);
		javaClass.append("@Override").append(oneTab);
		javaClass.append("public String toUpdateSQL() {").append(twoTab);
		javaClass.append("StringBuilder sql = new StringBuilder();").append(twoTab);

		List<String> primaryKeys = this.getPrimaryKey(tableName);
		if (primaryKeys.isEmpty()) {
			throw new IllegalArgumentException("表：" + tableName + "未定义主键");
		}
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		String columnName = null;
		javaClass.append("sql.append(\"update ").append(tableName).append(" set \")");
		int paramCount = 0;
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);
			if (!primaryKeys.contains(columnName)) {
				javaClass.append(".append(\"").append(columnName).append(" = ?,\")");
				paramCount++;
			}
		}
		javaClass.delete(javaClass.length() - 3, javaClass.length());
		javaClass.append("\").append(\" where \")");
		Collections.reverse(primaryKeys);
		for (String primaryKey : primaryKeys) {
			javaClass.append(".append(\"").append(primaryKey).append(" = \").append(").append("this.")
					.append(this.getFieldName(primaryKey)).append(").append(\" and \")");
		}
		javaClass.delete(javaClass.length() - 17, javaClass.length());
		javaClass.append(");").append(twoTab);
		javaClass.append("return sql.toString();").append(oneTab).append("}");

		javaClass.append(oneTab).append("@Override").append(oneTab).append("public Object[] toUpdateSQLParameters(){")
				.append(twoTab).append("Object[] parameters = new Object[").append(paramCount).append("];")
				.append(twoTab);
		int j = 0;
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);
			if (!primaryKeys.contains(columnName)) {

				javaClass.append("parameters[").append(j).append("] = ").append("this.")
						.append(this.getFieldName(columnName)).append(";").append(twoTab);
				j++;
			}
		}
		javaClass.append("return parameters;").append(oneTab).append("}");
	}

	private void createToInsertSQLMethod(StringBuilder javaClass, ResultSet rs, String tableName) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		String columnName = null;
		javaClass.append(oneTab).append("@Override").append(oneTab).append("public String toInsertSQL() {")
				.append(twoTab);
		javaClass.append("StringBuilder sql = new StringBuilder();").append(twoTab);
		javaClass.append("sql.append(\"insert into ").append(tableName).append(" (");
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);
			javaClass.append(columnName).append(",");
		}
		javaClass.delete(javaClass.length() - 1, javaClass.length());
		javaClass.append(") values (");
		int paramCount = 0;
		for (int i = 0; i < columnCount; i++) {
			javaClass.append("?").append(",");
			paramCount++;
		}
		javaClass.delete(javaClass.length() - 1, javaClass.length());
		javaClass.append(")\");").append(twoTab).append("return sql.toString();").append(oneTab).append("}");

		javaClass.append(oneTab).append("@Override").append(oneTab).append("public Object[] toInsertSQLParameters(){")
				.append(twoTab).append("Object[] parameters = new Object[").append(paramCount).append("];")
				.append(twoTab);
		for (int i = 0; i < columnCount; i++) {
			columnName = metaData.getColumnName(i + 1);
			javaClass.append("parameters[").append(i).append("] = ").append("this.")
					.append(this.getFieldName(columnName)).append(";").append(twoTab);
		}
		javaClass.append("return parameters;").append(oneTab).append("}");
	}

	private void createToDeleteSQL(StringBuilder javaClass, ResultSet rs, String tableName) {
		javaClass.append(oneTab);
		javaClass.append("@Override").append(oneTab).append("public String toDeleteSQL() {").append(twoTab)
				.append("StringBuilder sql = new StringBuilder();").append(twoTab).append("sql.append(\"delete from ")
				.append(tableName).append(" where \")");
		List<String> primaryKeys = this.getPrimaryKey(tableName);

		for (String primaryKey : primaryKeys) {
			javaClass.append(".append(\"").append(primaryKey).append(" = \").append(").append("this.")
					.append(this.getFieldName(primaryKey)).append(").append(\" and \")");
		}
		javaClass.delete(javaClass.length() - 17, javaClass.length());

		javaClass.append(");").append(twoTab).append("return sql.toString();").append(oneTab).append("}");
	}

	private void createToSelectSQL(StringBuilder javaClass, ResultSet rs, String tableName) throws SQLException {
		List<String> primaryKeys = this.getPrimaryKey(tableName);

		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		String primary = null;
		for (int i = 1; i <= colCount; i++) {
			String colName = metaData.getColumnName(i);
			if (primaryKeys.contains(colName)) {
				primary = colName;
				break;
			}
		}

		javaClass.append(oneTab).append("@Override").append(oneTab).append("public String toSelectSQL() {")
				.append(twoTab).append("return toSelectSQL(this.").append(this.getFieldName(primary)).append(");")
				.append(oneTab).append("}");

		javaClass.append(oneTab).append("public static String toSelectSQL(");
		// ResultSetMetaData metaData = rs.getMetaData();
		// int colCount = metaData.getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String colName = metaData.getColumnName(i);
			if (primaryKeys.contains(colName)) {
				int sqlTypeIndex = metaData.getColumnType(i);
				String sqlType = SQLUtil.getJavaType(sqlTypeIndex);
				javaClass.append(sqlType).append(" ").append(this.getFieldName(colName));
				// 只取第一个主键。
				break;
			}
		}
		javaClass.append("){").append(twoTab).append("StringBuilder sql = new StringBuilder();").append(twoTab)
				.append("sql.append(\"select * from ").append(tableName).append(" where ").append(primary)
				.append(" = \"").append(").append(").append(this.getFieldName(primary)).append(");").append(twoTab)
				.append("return sql.toString();").append(oneTab).append("}");

		if (primaryKeys.size() > 1) {
			javaClass.append(oneTab).append("public static String toSelectSQL(");
			for (int i = 1; i <= colCount; i++) {
				String colName = metaData.getColumnName(i);
				if (primaryKeys.contains(colName)) {
					int sqlTypeIndex = metaData.getColumnType(i);
					String sqlType = SQLUtil.getJavaType(sqlTypeIndex);
					javaClass.append(sqlType).append(" ").append(this.getFieldName(colName)).append(",");

				}
			}
			javaClass.delete(javaClass.length() - 1, javaClass.length());

			javaClass.append("){").append(twoTab).append("StringBuilder sql = new StringBuilder();").append(twoTab)
					.append("sql.append(\"select * from ").append(tableName).append(" where ");
			for (int i = 1; i <= colCount; i++) {
				String colName = metaData.getColumnName(i);
				if (primaryKeys.contains(colName)) {
					javaClass.append(colName).append(" = \"").append(").append(").append(this.getFieldName(colName))
							.append(").append(\" and ");
				}
			}
			javaClass.delete(javaClass.length() - 14, javaClass.length());
			javaClass.append(";").append(twoTab).append("return sql.toString();").append(oneTab).append("}");

		}

		javaClass.append(oneTab).append("public static String toSelectAllSQL(){").append(twoTab)
				.append("return \"select * from \" + tableName;").append(oneTab).append("}");

	}

	/**
	 * 
	 * @Desc 描述：获取一个表中的所有主键。
	 * @param tableName
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午10:43:02
	 *
	 */
	private List<String> getPrimaryKey(String tableName) {
		Connection connection = this.createConnection();
		DatabaseMetaData dmData;
		List<String> list = new ArrayList<>();
		try {
			dmData = connection.getMetaData();

			ResultSet rSet = dmData.getPrimaryKeys(null, null, tableName);
			while (rSet.next()) {
				String key = rSet.getString("COLUMN_NAME");
				list.add(key);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 
	 * @Desc 描述：把一个字段串，转化为类的命名方式，即首字母大写，如果带下划线，去掉下划线，分割的单词首字母也要大写。
	 * @param tableName
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:39:33
	 *
	 */
	private String getClassName(String tableName) {

		String[] strs = tableName.split("_");
		String className = "";
		if (strs.length >= 2) {

			for (String str : strs) {

				className += StringUtil.firstToUpper(str);
			}
		} else {
			className = StringUtil.firstToUpper(tableName);
		}
		return className;
	}

	/**
	 * 
	 * @Desc 描述：获取类中字段的名字，这是根据数据库表中的字段转化来过的。如果有下划线，会去掉下载线，并把首字母小写，
	 *       其它下划线分割的单词首字母大写
	 * @param fieldName
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:38:03
	 *
	 */
	private String getFieldName(String fieldName) {
		/*
		 * fieldName = fieldName.toLowerCase(); String[] strs =
		 * fieldName.split("_"); String className = ""; if (strs.length >= 2) {
		 * 
		 * for (String str : strs) {
		 * 
		 * className += StringUtil.firstToUpper(str); } className =
		 * StringUtil.firstToLower(className); } else {
		 * 
		 * className = StringUtil.firstToLower(fieldName); } return className;
		 */

		return fieldName;
	}

}
