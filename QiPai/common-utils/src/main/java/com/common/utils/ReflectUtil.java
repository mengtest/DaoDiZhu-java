package com.common.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectUtil {

	private static final String SET = "set";
	private static final String GET = "get";

	public static Object create(Class<?> cl) {
		try {
			return cl.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * <p>
	 * Title: getter
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param obj
	 *            操作的对象
	 * @param att
	 *            方法名（不带get，只有成员变量名）
	 * @author guangshuai.wang
	 */
	public static Object getValue(Object obj, String att) {
		Object result = null;
		try {
			Method method = obj.getClass().getMethod(GET + StringUtil.firstToUpper(att));
			result = method.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	/**
	 * 用反射调用set方法给对象实例赋值，这个方法只用于java bean
	 * <p>
	 * Title: setter
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param obj
	 *            要操作的对象
	 * @param methodName
	 *            不要加set的值，只写属性名即可。 方法名
	 * @param value
	 *            要赋的值
	 * @param argumentType
	 *            成员变量类型,注意int.class和Integer.class的区别，这是两个不同的类型
	 * @author guangshuai.wang
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	public static void setValue(Object obj, String methodName, Object value, Class<?> argumentType) {

		Method method;
		try {
			method = obj.getClass().getMethod(SET + methodName, argumentType);
			method.invoke(obj, value);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 此方法是获取一个对象中，所有非static的基本类型的字节总长度
	 *
	 * @param cl
	 * @return
	 * @author Terry
	 * @time 2016年6月12日
	 */
	public static int getObjectAllMemberBytesLen(Class<?> cl) {
		int len = 0;
		Field[] fields = cl.getDeclaredFields();
		for (Field field : fields) {
			// 不包括静成员变量
			if (!Modifier.isStatic(field.getModifiers())) {
				// 字段类型名
				String typeName = field.getType().getName();
				// 先判断基本类型的数据
				if (field.getType().isPrimitive()) {

					switch (typeName) {
					case "int":
						len += Integer.BYTES;
						break;
					case "boolean":
						len += 1;
						break;
					case "short":
						len += Short.BYTES;
						break;
					case "long":
						len += Long.BYTES;
						break;
					case "byte":
						len += Byte.BYTES;
						break;
					case "char":
						len += Character.BYTES;
						break;
					case "float":
						len += Float.BYTES;
						break;
					case "double":
						len += Double.BYTES;
						break;
					default:
						break;
					}
				}
			}

		}
		return len;
	}

	/**
	 * 
	 * 描述:这个方法是根据包路径，获取这个包下的所有class
	 *
	 * @param pack
	 * @return
	 * @author Terry
	 * @time 2016年7月26日-下午7:51:26
	 */
	public static List<Class<?>> getClasssFromPackage(String pack) {
		// 第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();

		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			if(dirs == null || !dirs.hasMoreElements()){
				return classes;
			}
			// 获取下一个元素
			URL url = dirs.nextElement();
			// 得到协议的名称
			String protocol = url.getProtocol();
			// 如果是以文件的形式保存在服务器上
			if ("file".equals(protocol)) {
				classes = getClassFromFile(packageName);
			} else if ("jar".equals(protocol)) {
				classes = getClassFromJar(url, packageName, packageDirName);
			}
		} catch (IOException e) {
			return null;
		}

		return classes;
	}

	private static List<Class<?>> getClassFromJar(URL url, String packageName, String packageDirName) {
		List<Class<?>> classes = new ArrayList<>();
		// 如果是jar包文件
		// 定义一个JarFile
		JarFile jar;
		// 是否循环迭代
		boolean recursive = true;
		try {
			// 获取jar
			jar = ((JarURLConnection) url.openConnection()).getJarFile();
			// 从此jar包 得到一个枚举类
			Enumeration<JarEntry> entries = jar.entries();
			// 同样的进行循环迭代
			while (entries.hasMoreElements()) {
				// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				// 如果是以/开头的
				if (name.charAt(0) == '/') {
					// 获取后面的字符串
					name = name.substring(1);
				}
				// 如果前半部分和定义的包名相同
				if (name.startsWith(packageDirName)) {
					int idx = name.lastIndexOf('/');
					// 如果以"/"结尾 是一个包
					if (idx != -1) {
						// 获取包名 把"/"替换成"."
						packageName = name.substring(0, idx).replace('/', '.');
					}
					// 如果可以迭代下去 并且是一个包
					if ((idx != -1) || recursive) {
						// 如果是一个.class文件 而且不是目录
						if (name.endsWith(".class") && !entry.isDirectory()) {
							// 去掉后面的".class" 获取真正的类名
							String className = name.substring(packageName.length() + 1, name.length() - 6);
							try {
								// 添加到classes
								classes.add(Class.forName(packageName + '.' + className));
							} catch (ClassNotFoundException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// log.error("在扫描用户定义视图时从jar包获取文件出错");
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 
	 * 描述:这个方法是从文件夹中获取指定包下的所有class,一般用于eclipse内部使用
	 *
	 * @param pack
	 * @return
	 * @author Terry
	 * @time 2016年7月26日-下午7:58:51
	 */
	private static List<Class<?>> getClassFromFile(String pack) {
		List<Class<?>> classes = new ArrayList<>();
		String path = ReflectUtil.class.getResource("/").getPath();
		String daopath = pack.replace(".", "/");
		File file = new File(path + "/" + daopath);
		if (!file.isDirectory()) {
			return classes;
		}

		File[] files = file.listFiles();
		for (File oneFile : files) {
			try {
				addClass(oneFile, pack, classes);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return classes;
	}
	
	

	private static void addClass(File file, String pck, List<Class<?>> classes) throws ClassNotFoundException {
		if (file.isDirectory()) {
			File[] classFiles = file.listFiles();
			for (File cfile : classFiles) {
				if (cfile.isDirectory()) {
					addClass(cfile, pck, classes);
				} else {
					String[] fileNames = cfile.getName().split("\\.");
					String filePath = cfile.getParent();
					String[] pathDirs = filePath.split("\\\\");
					boolean flag = false;
					for(String p : pathDirs){
						if(pck.endsWith(p)){
							flag = true;
							continue;
						}
						if(flag){
							pck += "." + p;
						}
					}
					String className = pck +"." + fileNames[0];
					Class<?> cl = Class.forName(className);
					classes.add(cl);
				}
			}
		} else {
			if (file.getName().endsWith(".class")) {
				String[] fileNames = file.getName().split("\\.");
				String className = pck + "." + fileNames[0];
				Class<?> cl = Class.forName(className);
				classes.add(cl);
			}
		}
	}

}
