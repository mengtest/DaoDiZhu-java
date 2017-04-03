package com.common.shutdown;

public interface IShutDown {
	/**
	 * 
	 * 描述:开始关闭清理资源
	 * @author wang guang shuai
	 * @Date   2016年12月27日下午3:29:55
	 * @return
	 */
	void shutDown();
	/**
	 * 
	 * 描述:判断是否清理完成。true完成，false未完成。
	 * @author wang guang shuai
	 * @Date   2016年12月27日下午3:30:11
	 * @return
	 */
	boolean isTerminated();
}
