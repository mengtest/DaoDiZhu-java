package com.common.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 描述:
 * 
 * @author wang guang shuai 2016年10月27日 上午11:32:16
 */
public class ScriptUtil {
	private final static String JAVA_SCRIPT = "javascript";
	private static ScriptEngineManager manager = new ScriptEngineManager();
	/**
	 * 
	 * 描述：获取javascript脚本引擎，这个引擎是非线程安全的。
	 *
	 * @return   
	 * @author wang guang shuai
	 *
	 * 2016年10月27日 上午11:45:17
	 *
	 */
	public static ScriptEngine getJavaScript() {
		return manager.getEngineByName(JAVA_SCRIPT);
	}

	public static void main(String[] args) {
		
		ScriptEngine engine = getJavaScript();
		engine.put("lv", 3);
		Number a;
		try {
			a = (Number) engine.eval("lv * 20.0");
			System.out.println(a.intValue());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
	}
}
