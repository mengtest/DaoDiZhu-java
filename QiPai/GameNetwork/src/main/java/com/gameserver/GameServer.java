package com.gameserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codec.DecodeCommandHandler;
import com.codec.EncodeCommandHandler;
import com.codec.ICommandSerializer;
import com.common.utils.ReflectUtil;
import com.config.NetworkConfig;
import com.handler.AbstractLoginHandler;
import com.handler.CommandHandlerFactory;
import com.handler.AbstractGameLogicHandler;
import com.handler.HeartbeatHandler;
import com.manager.Manager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class GameServer {
	private NioEventLoopGroup bossGroup = null;
	private NioEventLoopGroup workerGroup = null;
	private ICommandSerializer commandCodec = null;

	private NetworkConfig networkConfig;
	private ServerBootstrap b;
	private Class<? extends AbstractLoginHandler> loginHandlerClass;
	private AbstractGameLogicHandler gameLogicHandler = null;

	private List<Class<?>> managerClassList;

	/**
	 * 
	 * @param loginHandler
	 *            登陆验证的handler实现
	 * @param gameLogicHandler
	 *            游戏逻辑handler的实现
	 * @param commandSerializer
	 *            协议解码编码的实现
	 * @param networkConfig
	 *            网络通信系统配置数据
	 */
	public GameServer(Class<? extends AbstractLoginHandler> loginHandlerClass,
			AbstractGameLogicHandler gameLogicHandler, ICommandSerializer commandSerializer,
			NetworkConfig networkConfig) {
		this.commandCodec = commandSerializer;
		this.networkConfig = networkConfig;
		this.loginHandlerClass = loginHandlerClass;
		this.gameLogicHandler = gameLogicHandler;
	}

	/**
	 * 
	 * @Desc 注入消息处理handler的包路径
	 * @param requestPackagePath
	 * @author 王广帅
	 * @Date 2017年3月5日 上午11:59:31
	 */
	public void registerHandler(String handlerPackagePath) {
		CommandHandlerFactory.getInstance().registCommandHandler(handlerPackagePath);
	}

	public void registerManager(String handlerManagerPckPath) {
		List<Class<?>> classList = ReflectUtil.getClasssFromPackage(handlerManagerPckPath);
		for (Class<?> cl : classList) {
			Manager managerAnno = cl.getAnnotation(Manager.class);
			if (managerAnno != null) {
				managerClassList.add(cl);
			}
		}
	}

	public void initServer() {

		int bossThreads = networkConfig.getBossThreads();
		int workThreads = networkConfig.getWorkThreads();
		bossGroup = new NioEventLoopGroup(bossThreads);
		workerGroup = new NioEventLoopGroup(workThreads);
		b = new ServerBootstrap();

		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.ALLOW_HALF_CLOSURE, true)
				.childOption(ChannelOption.TCP_NODELAY, true);
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				p.addLast(new LengthFieldBasedFrameDecoder(10240, 0, 2, -2, 0));
				int idleTime = networkConfig.getIdleTime();
				p.addLast(new IdleStateHandler(idleTime, idleTime + 5, idleTime + 10));
				p.addLast(new DecodeCommandHandler(commandCodec));
				p.addLast(new EncodeCommandHandler(commandCodec));
				p.addLast(new HeartbeatHandler());
				p.addLast(createLoginHandler());
				p.addLast(gameLogicHandler);
			}
		});
	}

	/**
	 * 
	 * @Desc 创建一个登陆LoginHandler的实例对象。
	 * @return
	 * @author 王广帅
	 * @Date 2017年3月5日 下午12:23:31
	 */
	private AbstractLoginHandler createLoginHandler() {
		try {
			AbstractLoginHandler loginHandler = loginHandlerClass.newInstance();
			Map<String, Object> managerMap = this.createManagerObjec();
			loginHandler.setManagerMap(managerMap);
			return loginHandler;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @Desc 	创建有所有注入的管理类的实例。
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @author 王广帅
	 * @Date 2017年3月5日 下午12:27:32
	 */
	private Map<String, Object> createManagerObjec() throws InstantiationException, IllegalAccessException {
		Map<String, Object> map = new HashMap<>();
		for (Class<?> cl : managerClassList) {
			String key = cl.getInterfaces()[0].getSimpleName();
			Object obj = cl.newInstance();
			map.put(key, obj);
		}
		return map;
	}

	public void bind() {
		bind(null);
	}

	public void bind(ChannelFutureListener listener) {
		int port = networkConfig.getPort();
		ChannelFuture f;
		try {
			f = b.bind(port).sync();
			if (f != null && listener != null) {
				f.addListener(listener);
			}
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();

	}

	public boolean isTerminated() {
		return bossGroup.isTerminated() && workerGroup.isTerminated();
	}

}
