package com.common.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

public class NettyUtil {
	/**
	 * 
	 * 描述:获取客户端的ip
	 * @author wang guang shuai
	 * @Date   2016年12月15日下午7:01:40
	 * @param ctx
	 * @return
	 */
	public static String getClientIp(ChannelHandlerContext ctx){
		SocketAddress socketAddress = ctx.channel().remoteAddress();
		InetSocketAddress address = (InetSocketAddress)socketAddress;
		String ip = address.getAddress().getHostAddress();
		return ip;
	}
	/**
	 * 
	 * 描述:获取本地的ip地址。
	 * @author wang guang shuai
	 * @Date   2016年12月21日上午10:13:54
	 * @param ctx
	 * @return
	 */
	public static String getLocalIp(ChannelHandlerContext ctx){
		SocketAddress socketAddress = ctx.channel().localAddress();
		InetSocketAddress address = (InetSocketAddress)socketAddress;
		String ip = address.getAddress().getHostAddress();
		return ip;
	}
	
	public static ByteBuf pooledByteBuf(int capacity){
		return PooledByteBufAllocator.DEFAULT.buffer(capacity);
	}
	public static ByteBuf unpooledByteBuf(int capacity){
		return UnpooledByteBufAllocator.DEFAULT.buffer(capacity);
	}
	
	public static void release(ByteBuf byteBuf){
		if(byteBuf.refCnt() == 1){
			byteBuf.release();
		}
	}
	
}
