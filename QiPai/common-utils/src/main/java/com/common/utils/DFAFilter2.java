package com.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * 
 * @author wang guang shuai
 * @Date  2017年1月10日下午5:12:06
 */
public class DFAFilter2 {
	@SuppressWarnings("rawtypes")
	private Map sensitiveWordMap = null;

	// 最小匹配规则
	public static int minMatchTYpe = 1;

	// 最大匹配规则
	public static int maxMatchType = 2;

	private static final DFAFilter2 instance = new DFAFilter2();

	private DFAFilter2() {
	}

	public static DFAFilter2 getInstance() {
		return instance;
	}
	
	public void addSensitiveWordToHashMap(String config){
		String[] arr = readWords(config);
		this.addSensitiveWordToHashMap(arr);
	}
	private  String[] readWords(String config) {
		File file = new File(config);
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));
			StringBuffer sb = new StringBuffer();
			BufferedReader bufferedReader = new BufferedReader(read);
			String txt = null;
			// 读取文件，将文件内容放入到set中
			while ((txt = bufferedReader.readLine()) != null) {
				sb.append(txt);
			}
			bufferedReader.close();
			read.close();
			return sb.toString().split("、");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSensitiveWordToHashMap(String[] wordSet) {

		// 初始化敏感词容器，减少扩容操作
		sensitiveWordMap= new HashMap(wordSet.length);

		for (String word : wordSet) {
			Map nowMap = sensitiveWordMap;
			for (int i = 0; i < word.length(); i++) {

				// 转换成char型
				char keyChar = word.charAt(i);

				// 获取
				Object tempMap = nowMap.get(keyChar);

				// 如果存在该key，直接赋值
				if (tempMap != null) {
					nowMap = (Map) tempMap;
				}

				// 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
				else {

					// 设置标志位
					Map<String, String> newMap = new HashMap<String, String>();
					newMap.put("isEnd", "0");

					// 添加到集合
					nowMap.put(keyChar, newMap);
					nowMap = newMap;
				}

				// 最后一个
				if (i == word.length() - 1) {
					nowMap.put("isEnd", "1");
				}
			}
		}

	}

	/**
	 * 判断文字是否包含敏感字符
	 * 
	 * @param txt
	 * @param matchType
	 * @return
	 */
	public boolean isContaintSensitiveWord(String txt, int matchType) {
		boolean flag = false;
		for (int i = 0; i < txt.length(); i++) {

			// 判断是否包含敏感字符
			int matchFlag = this.CheckSensitiveWord(txt, i, matchType);

			// 大于0存在，返回true
			if (matchFlag > 0) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取文字中的敏感词
	 * 
	 * @param txt
	 * @param matchType
	 * @return
	 */
	private Set<String> getSensitiveWord(String txt, int matchType) {
		Set<String> sensitiveWordList = new HashSet<String>();

		for (int i = 0; i < txt.length(); i++) {

			// 判断是否包含敏感字符
			int length = CheckSensitiveWord(txt, i, matchType);

			// 存在,加入list中
			if (length > 0) {
				sensitiveWordList.add(txt.substring(i, i + length));

				// 减1的原因，是因为for会自增
				i = i + length - 1;
			}
		}

		return sensitiveWordList;
	}

	/**
	 * 替换敏感字字符
	 * 
	 * @param txt
	 * @param matchType
	 * @param replaceChar
	 * @return
	 */
	public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {

		String resultTxt = txt;

		// 获取所有的敏感词
		Set<String> set = getSensitiveWord(txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word = null;
		String replaceString = null;
		while (iterator.hasNext()) {
			word = iterator.next();
			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);
		}

		return resultTxt;
	}

	/**
	 * 获取替换字符串
	 * 
	 * @param replaceChar
	 * @param length
	 * @return
	 */
	private String getReplaceChars(String replaceChar, int length) {
		String resultReplace = replaceChar;
		for (int i = 1; i < length; i++) {
			resultReplace += replaceChar;
		}

		return resultReplace;
	}

	/**
	 * 检查文字中是否包含敏感字符，检查规则如下：<br>
	 * 如果存在，则返回敏感词字符的长度，不存在返回0
	 * 
	 * @param txt
	 * @param beginIndex
	 * @param matchType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {

		// 敏感词结束标识位：用于敏感词只有1位的情况
		boolean flag = false;

		// 匹配标识数默认为0
		int matchFlag = 0;
		Map nowMap = sensitiveWordMap;
		for (int i = beginIndex; i < txt.length(); i++) {
			char word = txt.charAt(i);

			// 获取指定key
			Object obj = nowMap.get(word);

			// 存在，则判断是否为最后一个
			if (obj != null) {
				nowMap = (Map)obj;
				// 找到相应key，匹配标识+1
				matchFlag++;

				// 如果为最后一个匹配规则,结束循环，返回匹配标识数
				if ("1".equals(nowMap.get("isEnd"))) {

					// 结束标志位为true
					flag = true;

					// 最小规则，直接返回,最大规则还需继续查找
					if (minMatchTYpe == matchType) {
						break;
					}
				}
			}

			// 不存在，直接返回
			else {
				break;
			}
		}

		// 长度必须大于等于1，为词
		if (matchFlag < 2 || !flag) {
			matchFlag = 0;
		}
		return matchFlag;
	}

	public static void main(String[] args) {


		DFAFilter2 filter = DFAFilter2.getInstance();
		String config = "config/key_word.txt";
		String str = "打破苏晨政法委组织国台政法委,Atan的移动石";
		String[] wordSet = DFAFilter.readWords(config);
		filter.addSensitiveWordToHashMap(wordSet);
		long start = System.currentTimeMillis();
		String hou = filter.replaceSensitiveWord(str, 1, "*");
		System.out.println(hou);
		long end = System.currentTimeMillis();
		System.out.println("用时：" + (end - start));
		
	}
}
