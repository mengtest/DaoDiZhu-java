package com.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * DFA 过滤算法实现敏感字符过滤，将敏感字符替换成*,这种写法是了快的，但是一个汉字会被替换成三个*
 * 
 * @author guanshuai.wang
 *
 **/
public class DFAFilter {

	/**
	 * 根结点
	 */
	private static DFATreeNode rootNode = new DFATreeNode();
	/**
	 * 关键字缓存
	 */
	private static ByteBuffer keyBuf = ByteBuffer.allocate(1024);
	/**
	 * 关键词编码
	 */
	private static String charset = "utf-8";

	private DFAFilter() {

	}

	public static String[] readWords(String config) {
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

	public static void createKeywordTree(String[] keywordList) {
		try {
			for (String keyword : keywordList) {

				byte[] bytes = keyword.trim().getBytes(charset);
				DFATreeNode tempNode = rootNode;
				for (int i = 0; i < bytes.length; i++) {
					int index = bytes[i] & 0xff;
					DFATreeNode node = tempNode.getSubNode(index);
					if (node == null) {
						node = new DFATreeNode();
						tempNode.setSubNode(index, node);
					}
					tempNode = node;
					if (i == bytes.length - 1) {
						tempNode.setEnd(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String searchKeyword(String content) {
		try {
			return searchKeyword(content.trim().getBytes(charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String searchKeyword(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		if (bytes == null || bytes.length == 0)
			return str.toString();
		DFATreeNode tempNode = rootNode;
		int rollback = 0;
		int position = 0;
		while (position < bytes.length) {
			int index = bytes[position] & 0xff;
			keyBuf.put(bytes[position]);
			tempNode = tempNode.getSubNode(index);
			if (tempNode == null) {
				position = position - rollback;
				rollback = 0;
				tempNode = rootNode;
				keyBuf.clear();
			} else if (tempNode.isEnd()) {
				keyBuf.flip();
				for (int i = 0; i <= rollback; i++) {
					bytes[position - i] = 42;
				}
				keyBuf.limit(keyBuf.capacity());
				rollback = 1;
			} else {
				rollback++;
			}
			position++;
		}
		String result = null;
		try {
			result = new String(bytes, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 这里的结果是以每个字节替换为一个*号的。一个汉字点三个字节，这里做一下处理，按汉字的个数显示。

		return result;
	}

	public void setCharset(String charset) {
		DFAFilter.charset = charset;
	}

	class Keyword {

		private int id;
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	static class DFATreeNode {
		/**
		 * 关键字的终结true,false 继续。
		 */
		private boolean end = false;
		private static int len = 256;
		private List<DFATreeNode> subNodes = new ArrayList<DFATreeNode>(len);

		public DFATreeNode() {
			for (int i = 0; i < len; i++) {
				subNodes.add(i, null);
			}
		}

		public List<DFATreeNode> getSubNodes() {
			return subNodes;
		}

		public void setSubNode(int index, DFATreeNode node) {
			subNodes.set(index, node);
		}

		public DFATreeNode getSubNode(int index) {
			return subNodes.get(index);
		}

		public void setEnd(boolean end) {
			this.end = end;
		}

		public boolean isEnd() {
			return end;
		}
	}

	public static void main(String[] args) {
		String str = "打破苏晨政法委组织国台Atan的移动石";
		String config = "config/key_word.txt";
		String[] keyArr = DFAFilter.readWords(config);
		DFAFilter.createKeywordTree(keyArr);
		long start = System.currentTimeMillis();
		System.out.println(DFAFilter.searchKeyword(str));
		long end = System.currentTimeMillis();
		System.out.println("用时：" + (end - start));
	}
}
