package com.common.log;

import com.common.log.GameLogFactory.GameLogger;

public abstract class AbstractGameLogger implements IGameLogger {

	public abstract GameLogger getGameLogger();

	public boolean isDebug() {
		return this.getGameLogger().isDebug();
	}

	@Override
	public void info(long playerId, Object... msgs) {
		this.getGameLogger().info(playerId, msgs);
	}

	@Override
	public void debug(long playerId, Object... msgs) {
		this.getGameLogger().debug(playerId, msgs);
	}

	@Override
	public void warn(long playerId, Object... msgs) {
		this.getGameLogger().warn(playerId, msgs);
	}

	@Override
	public void error(long playerId, Object... msgs) {

		this.getGameLogger().error(playerId, msgs);
	}

	@Override
	public void error(long playerId, Throwable exp, Object... msgs) {

		this.getGameLogger().error(playerId, exp, msgs);
	}

	@Override
	public void fatal(long playerId, Object... msgs) {
		this.fatal(playerId, msgs);
		
	}

	@Override
	public void fatal(long playerId, Throwable exp, Object... msgs) {
		this.fatal(playerId, exp, msgs);
		
	}
}
