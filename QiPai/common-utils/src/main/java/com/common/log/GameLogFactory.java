package com.common.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class GameLogFactory {
	private static String log4jDir = "log4j.configurationFile";
	
	static {
		System.setProperty(log4jDir, "config/log4j2.xml");
	}

	public static void refreshLogConfig() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.reconfigure();
	}

	public static void colseLog() {
		System.out.println("----开始关闭日志资源----");
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.close();
		context.stop();
		while (!context.isStopped()) {
		}

	}

	public static GameLogger newLogger(Class<?> clazz) {
		return new GameLogger(clazz);
	}

	public static GameLogger newLogger(String name) {
		return new GameLogger(name);
	}

	public static void main(String[] args) {
		String logName = "RequestAndResponseLog";
		GameLogger gameLogger = newLogger(logName);

		gameLogger.info(12, "test1");
	}

	public static class GameLogger implements IGameLogger {

		private Logger logger;

		private GameLogger(Class<?> clazz) {
			logger = LogManager.getLogger(clazz);
		}

		private GameLogger(String name) {
			logger = LogManager.getLogger(name);
		}

		public String getLogMsg(long playerId, Object... msgs) {
			if (msgs != null) {
				StringBuilder resultBuild = new StringBuilder();
				if (playerId != 0) {
					resultBuild.append(playerId).append("->");
				}
				for (Object msg : msgs) {
					resultBuild.append(msg);
				}
				return resultBuild.toString();
			}
			return null;
		}

		@Override
		public void info(long playerId, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.info(log);
			}
		}

		@Override
		public void debug(long playerId, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.debug(log);
			}
		}

		@Override
		public void warn(long playerId, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.warn(log);
			}
		}

		@Override
		public void error(long playerId, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.error(log);
			}
		}

		@Override
		public void error(long playerId, Throwable exp, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.error(log, exp);
			}
		}

		@Override
		public void fatal(long playerId, Object... msgs) {
			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.fatal(log);
			}
		}

		@Override
		public void fatal(long playerId, Throwable exp, Object... msgs) {

			String log = this.getLogMsg(playerId, msgs);
			if (log != null) {
				logger.fatal(log, exp);
			}
		}

		public Level getLevel() {
			return logger.getLevel();
		}

		public boolean isDebug() {
			return this.getLevel() == Level.DEBUG;
		}
	}

}
