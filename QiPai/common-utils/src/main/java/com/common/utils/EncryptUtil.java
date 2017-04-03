package com.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Charsets;

/**
 * @Desc  描述:
 * @author wang guang shuai
 * @Date 2016年9月20日 上午10:25:53
 */
public class EncryptUtil {
	/**
	 * 
	 * @Desc  描述：对一个字符串进行md5
	 * @param value
	 * @return   返回的是md5后的十六进制字符串
	 * @author wang guang shuai
	 * @date 2016年9月20日 上午10:56:14
	 *
	 */
	public static String MD5(String value){
		return MD5(value.getBytes(Charsets.UTF_8));
	}
	/**
	 * 
	 * @Desc  描述：对一个byte数组md5,
	 * @param resources
	 * @return   返回的是md5后的十六进制字符串
	 * @author wang guang shuai
	 * @date 2016年9月20日 上午10:56:45
	 *
	 */
	public static String MD5(byte[] resources){
		String result = null;
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			md5Digest.update(resources);
			byte[] degist = md5Digest.digest();
			result = ByteUtil.bytesToHexString(degist);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(MD5("aaa").length());
	}
}
