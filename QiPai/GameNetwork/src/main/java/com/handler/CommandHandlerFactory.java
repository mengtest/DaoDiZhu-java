package com.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.command.AbstractCommand;
import com.command.Command;
import com.command.CommandID;
import com.common.utils.ReflectUtil;

/**
 * @Desc 描述:存储每个命令的handler实现。
 * @author wang guang shuai
 * @Date 2016年9月27日 下午1:45:47
 */
public class CommandHandlerFactory {

	private Map<Integer, ICommandHandler> handlerMap = new HashMap<>();

	private static CommandHandlerFactory instance = new CommandHandlerFactory();

	private CommandHandlerFactory() {
	}

	public static CommandHandlerFactory getInstance() {
		return instance;
	}

	/**
	 * 
	 * @Desc 描述：注册相应的命令处理类
	 * @param pckPath
	 *            命令处理类所在的包路径
	 * @author wang guang shuai
	 * @date 2016年9月27日 下午1:47:12
	 *
	 */
	public void registCommandHandler(String pckPath) {
		List<Class<?>> list = ReflectUtil.getClasssFromPackage(pckPath);
		if (list != null) {
			for (Class<?> cl : list) {
				Class<?> superCl = cl.getSuperclass();
				Command command = superCl.getAnnotation(Command.class);
				if (command == null) {
					throw new NullPointerException(cl.getName() + "未添加命令注解");
				}
				Class<? extends AbstractCommand> cmd = command.value();
				CommandID commandID = cmd.getAnnotation(CommandID.class);
				int id = commandID.ID();
				try {
					Object obj = cl.newInstance();
					if (obj instanceof ICommandHandler) {
						ICommandHandler commandHandler = (ICommandHandler) obj;
						handlerMap.put(id, commandHandler);
					}

				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ICommandHandler getCommandHandler(Integer commandId) {
		return handlerMap.get(commandId);
	}
}
