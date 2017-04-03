package com.common.utils;

import java.util.Random;

/**
 * 游戏随机工具类<br>
 */
public class RandomUtil {
	/**
	 * 在一个范围中随机一个整数(包括两端的值)
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max) {
		if (min == max) {
			return min;
		}
		Random r = new Random();
		int intValue = max - min;
		intValue = intValue < 0 ? 1 : intValue + 1;
		return min + r.nextInt(intValue);
	}

	public static byte randomByte(byte min, byte max) {
		int r = randomInt(min, max);

		if (min > Byte.MAX_VALUE) {
			r = randomInt(0, Byte.MAX_VALUE);
		} else if (r > Byte.MAX_VALUE) {
			r = randomInt(min, Byte.MAX_VALUE);
		}
		return (byte) r;
	}

	/**
	 * 
	 * 描述：在范围内随机一个小数，这个值不包括两端的值
	 *
	 * @param min
	 * @param max
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月17日 上午10:13:14
	 *
	 */
	public static float randomFloat(float min, float max) {
		if (min == max) {
			return min;
		}
		Random r = new Random();
		float value =  min + (max - min) * r.nextFloat();
		return value;
	}
	

	/**
	 * 通过百分比随机，看是否中
	 * 
	 * @author suyinglong
	 * @version 2015年4月8日下午4:04:03
	 * @param percent
	 * @return 返回“中不中”
	 */
	public static boolean randomByPercent(float percent) {
		int num = new Float(percent * 100).intValue();
		int r = randomInt(1, 100);
		return r <= num;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			float value = RandomUtil.randomFloat(0.01f, 0.02f);
			System.out.println(value);
		}
	}

}
