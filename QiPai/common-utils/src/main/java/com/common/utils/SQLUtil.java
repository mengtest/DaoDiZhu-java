package com.common.utils;

import java.sql.Types;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年9月22日 上午9:58:33
 */
public class SQLUtil {
	/**
	 * 
	 * @Desc  描述：根据mysql类型，获取对应的java类型
	 * @param sqlType
	 * @return   
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:58:58
	 *
	 */
	public static String getJavaType(int sqlType){
		String type = null;
		switch (sqlType) {
		case Types.BOOLEAN:
		case Types.BIT:
			//ReflectUtil.setValue(t, methodName, (Boolean) value, Boolean.class);
			type = "Boolean";
			break;
		case Types.TINYINT:
			//ReflectUtil.setValue(t, methodName, ((Integer) value).byteValue(), Byte.class);
			type = "Byte";
			break;
		case Types.SMALLINT:
			//ReflectUtil.setValue(t, methodName, ((Integer) value).shortValue(), Short.class);
			type = "Short";
			break;
		case Types.INTEGER:
			//ReflectUtil.setValue(t, methodName, (Integer) value, Integer.class);
			type = "Integer";
			break;
		case Types.BIGINT:
			//ReflectUtil.setValue(t, methodName, (Long) value, Long.class);
			type = "Long";
			break;
		case Types.FLOAT:
		case Types.REAL:
			//ReflectUtil.setValue(t, methodName, (Float) value, Float.class);
			type = "Float";
			break;
		case Types.NUMERIC:
		case Types.DECIMAL:
			type = "BigDecimal";
			//value = ((BigDecimal) value).doubleValue();
			break;
		case Types.DOUBLE:
			//ReflectUtil.setValue(t, methodName, (Double) value, Double.class);
			type = "Double";
			break;
		case Types.VARCHAR:
		case Types.CHAR:
		case Types.LONGVARCHAR:
			//ReflectUtil.setValue(t, methodName, (String) value, String.class);
			type = "String";
			break;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			type = "byte[]";
			//ReflectUtil.setValue(t, methodName, (byte[]) value, byte[].class);
			break;
		case Types.DATE:
			//ReflectUtil.setValue(t, methodName, (Date) value, Date.class);
			type = "Date";
			break;
		case Types.TIME:
			//ReflectUtil.setValue(t, methodName, (Time) value, Time.class);
			type = "Time";
			break;
		case Types.TIMESTAMP:
			//ReflectUtil.setValue(t, methodName, (Timestamp) value, Timestamp.class);
			type = "Timestamp";
			break;
		default:
			throw new IllegalArgumentException("数据库暂时不支持的类型：" + sqlType);
		}
		return type;
	}
}
