package com.common.utils;

import java.io.File;

import com.thoughtworks.xstream.XStream;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年12月8日 上午10:47:24
 */
public class XmlUtil {

	public static <T> T xmlToBean(String path, Class<T> t) {
		return xmlToBean(new File(path), t);
	}

	public static <T> T xmlToBean(File file, Class<T> t) {
		XStream stream = new XStream();
		stream.processAnnotations(t);
		@SuppressWarnings("unchecked")
		T result = (T) stream.fromXML(file);
		return result;
	}
}
