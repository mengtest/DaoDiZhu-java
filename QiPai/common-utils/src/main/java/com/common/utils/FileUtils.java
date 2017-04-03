package com.common.utils;

import java.io.File;

/**
 * 
 * 描述:
 * 
 * @author 王广帅
 *
 *         2017年3月1日 下午10:30:14
 */
public class FileUtils {

	/**
	 * 
	 * @Desc 检测一个文件夹路径是否存在，如果不存在，则重新创建
	 * @param path
	 * @author 王广帅
	 * @Date 2017年3月1日 下午10:27:57
	 */
	public static void checkAndCreateDir(String path) {
		File dir = new File(path);
		if (dir.isDirectory()) {
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} else {
			throw new IllegalArgumentException(path + " 不是一个文件夹");
		}
	}

	/**
	 * 
	 * @Desc 删除并重新创建一个文件夹
	 * @param path
	 * @author 王广帅
	 * @Date 2017年3月1日 下午10:33:52
	 */
	public static void deleteAndCreateDir(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			if (dir.isDirectory()) {

				File[] files = dir.listFiles();
				if (files.length > 0) {
					for (File file : files) {
						try {
							file.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				throw new IllegalArgumentException(path + " 不是一个文件夹");
			}

		} else {
			dir.mkdirs();
		}

	}
}
