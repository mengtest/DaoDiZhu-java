package com.common.utils;

import java.math.BigDecimal;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年11月17日 上午10:24:06
 */
public class NumberUtil {

	/**
	 * 
	 * 描述：四舍五入一个小数。
	 *
	 * @param value
	 *            要操作的值
	 * @param length
	 *            要保留的小数位
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月17日 上午10:28:12
	 *
	 */
	public static float roundFloat(float value, int length) {
		BigDecimal b = new BigDecimal(value);
		float result = b.setScale(length, BigDecimal.ROUND_HALF_UP).floatValue();
		return result;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(NumberUtil.roundFloat(0.1234546f, 4));
	}
}
