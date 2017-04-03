package com.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Desc 描述:Java的时间功能管理 Java时间设为二十四小时制和十二小时制的区别：<br/>
 * 
 * 1） 二十四小时制： yyyy-MM-dd HH:mm:ss <br/> 
 * 2）十二小时制： yyyy-MM-dd hh:mm:ss
 * @author wang guang shuai
 * @Date 2016年9月23日 下午6:37:04
 */
public class DateUtil {
	/** 15天的秒数 **/
	public static int DAY_15 = 15 * 24 * 60 * 60;
	/** 一天的秒数 **/
	public static int DAY_1 = 24 * 60 * 60;
	/** 半小时的秒数 **/
	public static int HALF_HOUR_SECOND = 30 * 60;
	/** 半小时的毫秒数 **/
	public static int HALF_HOUR_MILL = HALF_HOUR_SECOND * 1000;

	private static final String HHMMSS = "HH:MM:SS";
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 
	 * 描述：判断这个时间与当前时间跨越的天数
	 *
	 * @param time
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午3:05:16
	 *
	 */
	public static int interalDays(long time) {
		long nowTime = System.currentTimeMillis();
		return interalDays(time, nowTime);
	}
	/**
	 * 
	 * 描述：获取当前时间的字符串格式时间：yyyy-MM-dd HH:mm:ss
	 *
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年11月3日 下午7:01:28
	 *
	 */
	public static String getNowDateStr(){
		Date date = new Date();
		return dateToStr(date);
	}
	/**
	 * 
	 * 描述：date转string字符串，格式：yyyy-MM-dd HH:mm:ss
	 *
	 * @param date
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年11月3日 下午6:59:52
	 *
	 */
	public static String dateToStr(Date date){
		SimpleDateFormat formate = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		return formate.format(date);
		
	}

	/**
	 * 
	 * 描述：判断这两个时间之间间隔的天数,time1 小于time2
	 *
	 * @param time1
	 * @param time2
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午3:06:13
	 *
	 */
	public static int interalDays(long time1, long time2) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTimeInMillis(time1);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

		aCalendar.setTimeInMillis(time2);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return Math.abs(day2 - day1);
	}

	/**
	 * 
	 * 描述：获取time毫秒的时间格式：hh:mm:ss
	 *
	 * @param time
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午3:12:31
	 *
	 */
	public static String getHHMMSSTime(long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(HHMMSS);
		return format.format(date);
	}

	/**
	 * 
	 * 描述：获取当前毫秒对应的时间格式：hh:mm:ss
	 *
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午3:16:07
	 *
	 */
	public static String getHHMMSSTime() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(HHMMSS);
		return format.format(date);
	}

	/**
	 * 
	 * 描述：字符串格式的日期转为毫秒数：yyyy-mm-dd hh:mm:ss
	 *
	 * @param dateStr
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午3:51:21
	 * @throws ParseException
	 *
	 */
	public static long strToMillis(String dateStr) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		Date date = format.parse(dateStr);
		return date.getTime();
	}

	/**
	 * 
	 * 描述：将毫秒数的时间转为字符串时间：yyyy-mm-dd hh:mm:ss
	 *
	 * @param time
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午4:03:22
	 *
	 */
	public static String millisToStr(long time) {
		Date date = new Date(time);
		SimpleDateFormat formate = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		return formate.format(date);

	}

	/**
	 * 
	 * 描述：获取pretime开始，到下个最近的重置点hhmmss的毫秒数。也这个方法一般用于定点重置的功能。比如每天8点重置一次。
	 * 这个返回值就是重置点的毫秒数。
	 *
	 * @param preTime
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月3日 下午4:24:49
	 *
	 */
	public static long getNextResetTime(long preTime, String hhmmss) {
		Calendar calendar = Calendar.getInstance();
		String timeStr = getHHMMSSTime(preTime);
		calendar.setTimeInMillis(preTime);
		String[] strs = hhmmss.split(StringUtil.COLON);
		int i = 0;
		int hour = StringUtil.valueOfInt(strs[i++]);
		int minute = StringUtil.valueOfInt(strs[i++]);
		int second = StringUtil.valueOfInt(strs[i++]);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		if (timeStr.compareTo(hhmmss) > 0) {
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, day + 1);
		}
		return calendar.getTimeInMillis();
	}

	public static void main(String[] args) {
		String s1 = "2016-11-01 15:15:00";
		try {
			long s1time = strToMillis(s1);
			long result = getNextResetTime(s1time, "18:00:00");
			System.out.println(millisToStr(result));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
