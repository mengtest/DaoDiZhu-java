package com.start;

import java.io.IOException;

import com.common.log.GameLogFactory;
import com.common.log.GameLogFactory.GameLogger;
import com.common.mysql.MysqlFactory;
import com.common.redis.RedisFactory;
import com.config.NetworkConfig;
import com.config.NetworkConfigFactory;
import com.dao.DaoFactory;
import com.gameserver.GameServer;
import com.logic.GameUserHandler;
import com.logic.LoginHandler;
import com.network.codec.CommandCodec;
import com.network.config.ConfigFactory;

public class ServerStartMain {
	private static GameLogger gameLogger = GameLogFactory.newLogger(ServerStartMain.class);

	public static void main(String[] args) {
		ConfigFactory.initGameServerConfig("config/game_server_config.xml");
		
		gameLogger.info(0, "服务开始了.....!");
		int i = 1;
		gameLogger.info(0, (i++) + "，加载redis配置文件");
		RedisFactory.initRedis("config/redis_config.xml");
		try {

			gameLogger.info(0, (i++) + "，加载mysql配置文件");
			MysqlFactory.init("config/mysql_config.properties");
			gameLogger.info(0, (i++) + "，加载注册的Dao接口");

			gameLogger.info(0, (i++) + "，加载逻辑处理Handler");

			gameLogger.info(0, (i++) + "，启动dao定时同步到数据库的定时器");
			DaoFactory.registerDao();
			NetworkConfigFactory.getInstance().initNetworkConfig("config/network_config.xml");
			NetworkConfig networkConfig = NetworkConfigFactory.getInstance().getNetworkConfig();
			GameServer gameServer = new GameServer(LoginHandler.class,new GameUserHandler(), new CommandCodec(), networkConfig);
			gameServer.registerHandler("com.auto.command");
			gameServer.registerManager("com.logic.manager.impl");
			gameServer.initServer();
			gameLogger.info(0, "最后一步，在线程中启动服务成功,当前版本号", "1.0.0.2");
			gameServer.bind();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("服务启动失败，程序已退出");
			System.exit(0);
		}

	}
}
