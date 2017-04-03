package com.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.utils.ReflectUtil;

public class CommandFactory {

	private static Map<Integer, Class<? extends ICommand>> commandMap = new HashMap<>();

	public static void registerCommandClass(String commandPackagePath){
		List<Class<?>> clazzList = ReflectUtil.getClasssFromPackage(commandPackagePath);
		for(Class<?> clazz : clazzList){
			@SuppressWarnings("unchecked")
			Class<? extends ICommand> commandClazz = (Class<? extends ICommand>) clazz;
			registerCommandClass(commandClazz);
		}
	}

	/**
	 * 注册命令。
	 * 
	 * @param commandClazz
	 */
	public static void registerCommandClass(Class<? extends ICommand> commandClazz) {

		CommandID commandIdAnnotation = commandClazz.getAnnotation(CommandID.class);
		if (commandIdAnnotation != null) {
			int commandId = commandIdAnnotation.ID();
			CommandType commandType = commandIdAnnotation.type();
			int key = generateCommandMapKey(commandId, commandType);
			commandMap.put(key, commandClazz);
		}

	}

	/**
	 * 根据命令的id和类型，组成一个存储的key
	 * 
	 * @param commandId
	 * @param commandType
	 * @return
	 */
	public static int generateCommandMapKey(int commandId, CommandType commandType) {
		int type = commandType.ordinal();
		int result = commandId + (type << 31);
		return result;
	}

	/**
	 * 根据命令ID，创建一个新的命令。
	 * 
	 * @param commandId
	 * @return
	 */
	public static ICommand createNewCommand(Integer commandId) {
		Class<? extends ICommand> commandClazz = commandMap.get(commandId);
		if (commandClazz != null) {
			try {
				Object obj = commandClazz.newInstance();
				return (ICommand) obj;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		throw new NullPointerException("未找到CommandId为" + commandId + "的Command 命令对象");

	}
}
