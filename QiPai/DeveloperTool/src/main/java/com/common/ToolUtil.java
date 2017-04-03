package com.common;

import com.common.utils.StringUtil;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年10月10日 下午1:19:54
 */
public class ToolUtil {

	/**
	 * 
	 * @Desc 描述：把一个字段串，转化为类的命名方式，即首字母大写，如果带下划线，去掉下划线，分割的单词首字母也要大写。
	 * @param tableName
	 * @return
	 * @author wang guang shuai
	 * @date 2016年9月22日 上午9:39:33
	 *
	 */
	public static String getClassName(String tableName) {
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
	public static String getFieldName(String fieldName) {
		String[] strs = fieldName.split("_");
		String className = "";
		if (strs.length >= 2) {

			for (String str : strs) {

				className += StringUtil.firstToUpper(str);
			}
			className = StringUtil.firstToLower(className);
		} else {

			className = StringUtil.firstToLower(fieldName);
		}
		return className;
	}
}
