package com.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年11月19日 下午3:30:19
 */
public class SerializeUtil {
	/**
	 * 
	 * 描述：序列化一个对象
	 *
	 * @param obj
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年11月19日 下午3:38:49
	 *
	 */
	public static byte[] objToByte(Object obj) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(out);
			stream.writeObject(obj);
			byte[] byts = out.toByteArray();
			stream.close();
			out.close();
			return byts;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * 描述：反序列化一个对象
	 *
	 * @param bytes
	 * @param t
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年11月19日 下午3:38:37
	 *
	 */
	@SuppressWarnings("unchecked")
	public static <T> T byteToObj(byte[] bytes, Class<T> t) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ObjectInputStream objInput = new ObjectInputStream(in);
			T obj = (T) objInput.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
