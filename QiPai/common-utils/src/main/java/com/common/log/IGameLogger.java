package com.common.log;

public interface IGameLogger {
	void info(long playerId, Object... msgs);

	void debug(long playerId, Object... msgs);

	void warn(long playerId, Object... msgs);

	void error(long playerId, Object... msgs);

	void error(long playerId, Throwable exp, Object... msgs);

	void fatal(long playerId, Object... msgs);

	void fatal(long playerId, Throwable exp, Object... msgs);
}
