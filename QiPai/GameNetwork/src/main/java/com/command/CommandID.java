package com.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Desc  描述:commandid的注解，用于标记一个commandid的值
 * @author wang guang shuai
 * @Date 2016年9月18日 上午11:39:44
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandID {
	int ID();
	CommandType type();
}
