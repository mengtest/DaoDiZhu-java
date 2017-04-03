package com.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Desc  描述:通信命令的注解，用于标记一个命令。
 * @author wang guang shuai
 * @Date 2016年9月18日 下午2:15:13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	Class<? extends AbstractCommand> value();
}
