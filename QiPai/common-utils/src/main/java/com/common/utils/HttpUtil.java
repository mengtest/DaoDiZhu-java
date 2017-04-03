package com.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Desc 描述:
 * @author wang guang shuai
 * @Date 2016年9月22日 下午6:41:10
 */
public class HttpUtil {
	// 接收的最大包
	private static final int max_buff = 2048;

	/**
	 * 
	 * 描述：请求http,obj是请求的参数对象，t是返回结果的对象。对象会以json的格式被发送过去。
	 *
	 * @param url
	 * @param obj
	 * @param t  返回的数据对象的类型。如果是null，则返回null
	 * @return
	 * @author wang guang shuai
	 *
	 *         2016年11月29日 上午11:37:12
	 * @throws IOException
	 *
	 */
	public static <T> T post(String url, Object obj, Class<T> t) throws IOException {
		String str = JsonUtil.objToJson(obj);
		byte[] data = str.getBytes();
		data = post(url, data);
		if (t != null && data != null) {
			str = new String(data);
			return JsonUtil.jsonToObj(str, t);
		}
		return null;
	}

	public static byte[] post(String urlPath, byte[] data) throws IOException {
		URL url = new URL(urlPath);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

		// //设置连接属性
		httpConn.setDoOutput(true);// 使用 URL 连接进行输出
		httpConn.setDoInput(true);// 使用 URL 连接进行输入
		httpConn.setUseCaches(false);// 忽略缓存
		httpConn.setRequestMethod("POST");// 设置URL请求方法
		httpConn.setConnectTimeout(8000);
		httpConn.setReadTimeout(8000);

		// 设置请求属性
		// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
		httpConn.setRequestProperty("Content-length", String.valueOf(data.length));
		httpConn.setRequestProperty("Content-Type", "application/json");
		httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		httpConn.setRequestProperty("Charset", "UTF-8");
		// 建立输出流，并写入数据
		OutputStream outputStream = httpConn.getOutputStream();
		outputStream.write(data);
		InputStream inputStream = null;
		try {
			// 获得响应状态
			int responseCode = httpConn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
				// 每次最大读取的字节数
				byte[] buff = new byte[128];

				inputStream = httpConn.getInputStream();
				int count = 0;
				int total = 0;
				ByteBuffer ioBuff = ByteBuffer.allocate(max_buff);
				while (count != -1) {
					count = inputStream.read(buff);
					if (count != -1) {
						total += count;
						if (total > max_buff) {
							throw new IllegalArgumentException("http接收的包太多，超过了" + max_buff);
						}
						ioBuff.put(buff, 0, count);
					}
				}
				ioBuff.flip();
				if (total > 0) {
					byte[] result = Arrays.copyOf(ioBuff.array(), total);
					return result;
				}
			}
		} finally {
			outputStream.close();
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return null;
	}

	/**
	 * 
	 * 描述：读取request中的字节
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 * @author wang guang shuai
	 *
	 *         2016年11月29日 下午12:04:15
	 *
	 */
	public static byte[] httpRead(HttpServletRequest request) throws IOException {
		InputStream input = request.getInputStream();
		int len = request.getContentLength();
		if (len <= 0) {
			return null;
		}
		byte[] buffer = new byte[len];
		int count = input.read(buffer, 0, buffer.length);
		input.close();
		if (count > 0) {
			return buffer;
		}
		return null;
	}
	public static String httpReadJson(HttpServletRequest request) throws IOException{
		byte[] bytes = httpRead(request);
		if(bytes != null){
			String result = new String(bytes);
			return result;
		}
		return null;
	}

	public static void response(HttpServletResponse response, Object obj) throws IOException {
		if (obj == null) {
			return;
		}
		String json = JsonUtil.objToJson(obj);
		byte[] data = json.getBytes();
		OutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		out.close();
	}
}
