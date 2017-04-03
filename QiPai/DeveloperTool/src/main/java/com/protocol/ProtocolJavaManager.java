package com.protocol;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.common.JavaImport;
import com.common.ToolUtil;
import com.common.utils.FileUtils;
import com.common.utils.StringUtil;
import com.common.utils.XmlUtil;
import com.protocol.model.BeanModel;
import com.protocol.model.ProtocolModel;
import com.protocol.model.StructModel;
import com.protocol.model.VarModel;

public class ProtocolJavaManager {
	private final static String onetab = "\n\t";
	private final static String twotab = "\n\t\t";
	private final static String thirdtab = "\n\t\t\t";

	public static String xmlPath = null;
	public static String projectSrc = null;
	public static String commandPackage = "com.auto.command";
	public static String requestPackage = "com.auto.abstractrequest";
	private static String commandPath = projectSrc + File.separator + commandPackage.replace(".", File.separator);
	private static String requestPath = projectSrc + File.separator + requestPackage.replace(".", File.separator);

	public static void main(String[] args) {

		if (args.length == 0) {
			// xmlPath = "config/xml";
			xmlPath = "config/xml";
			projectSrc = "F:\\wgs\\qipai\\server\\QiPai\\QiPaiCommand\\src\\main\\java";

			commandPath = projectSrc + File.separator + commandPackage.replace(".", File.separator);
			requestPath = projectSrc + File.separator + requestPackage.replace(".", File.separator);

		}
		builder();
	}


	private static void builder() {
		File xmlDir = new File(xmlPath);
		if (!xmlDir.isDirectory()) {
			System.out.println("协议配置文件夹路径错误：" + xmlPath);
		}
		File[] xmlFiles = xmlDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		for (File xmlFile : xmlFiles) {
			ProtocolModel protocolModel = XmlUtil.xmlToBean(xmlFile, ProtocolModel.class);
			buildProtoBuffer(protocolModel);
			buildCommand(protocolModel);
			buildCommandHandler(protocolModel);
		}
	}

	/**
	 * 
	 * 描述:根据协议配置文件，把struct和request,response生成protobuf配置文件，并使用protobuf的工具生成Java类。
	 * 
	 * @author wang guang shuai
	 * @Date 2016年12月14日下午2:47:37
	 * @param protocolModel
	 */
	private static void buildProtoBuffer(ProtocolModel protocolModel) {
		StringBuilder protoBuild = new StringBuilder();
		String protobufClass = commandPackage + ".protos";
		protoBuild.append("option java_package = \"").append(protobufClass).append("\";\n");
		protoBuild.append("option java_outer_classname = \"").append(protocolModel.getClassName()).append("Proto\";\n");
		if (protocolModel.getStructList() != null && !protocolModel.getStructList().isEmpty()) {
			for (StructModel structModel : protocolModel.getStructList()) {
				protoBuild.append("message ").append(structModel.getName()).append("{").append(onetab);
				setVarvalue(structModel.getVarList(), protoBuild);
				protoBuild.append("}\n");
			}
		}

		for (BeanModel beanModel : protocolModel.getBeanList()) {
			if (beanModel.getRequestModel().getVarList() != null
					&& !beanModel.getRequestModel().getVarList().isEmpty()) {
				protoBuild.append("message ").append(beanModel.getName() + "Request").append("{").append(onetab);
				setVarvalue(beanModel.getRequestModel().getVarList(), protoBuild);
				protoBuild.append("}\n");
			}
			if (beanModel.getResponseModel().getVarList() != null
					&& !beanModel.getResponseModel().getVarList().isEmpty()) {

				protoBuild.append("message ").append(beanModel.getName() + "Response").append("{").append(onetab);
				setVarvalue(beanModel.getResponseModel().getVarList(), protoBuild);
				protoBuild.append("}\n");
			}
		}
		String protofiles = "config\\protofiles";
		FileUtils.deleteAndCreateDir(protofiles);
		String pathFile = protofiles  + File.separator + protocolModel.getClassName() + ".proto";
		writeFile(pathFile, protoBuild);
		System.out.println("----生成protobuf文件成功->" + pathFile + "----");
		
		// 根据protobuf配置文件生成java代码
		String cmd = "config\\\\protobuf\\\\protoc.exe --java_out=" + projectSrc + " " + pathFile;
		System.out.println(cmd);
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setVarvalue(List<VarModel> varModelList, StringBuilder build) {

		int i = 1;
		for (VarModel varModel : varModelList) {
			build.append(varModel.getAttr()).append(" ").append(varModel.getType()).append(" ")
					.append(varModel.getName()).append(" = ").append(i).append(";").append(onetab);
			i++;
		}
		build.append("\n");
	}

	private static void buildCommand(ProtocolModel protocolModel) {
		for (BeanModel beanModel : protocolModel.getBeanList()) {
			List<VarModel> varModels = null;
			if (beanModel.getRequestModel() != null) {
				varModels = beanModel.getRequestModel().getVarList();
			}
			if (varModels == null) {
				varModels = new ArrayList<>();
			}
			createCommand(protocolModel, beanModel, varModels, 1);
			varModels = null;
			if (beanModel.getResponseModel() != null) {
				varModels = beanModel.getResponseModel().getVarList();
			}
			if (varModels == null) {
				varModels = new ArrayList<>();
			}
			createCommand(protocolModel, beanModel, varModels, 2);
		}
	}

	private static void createCommand(ProtocolModel protocolModel, BeanModel beanModel, List<VarModel> varModels,
			int type) {
		String commandPathOfModule = commandPath + File.separator + protocolModel.getClassName().toLowerCase();
		String commandPkgOfModule = commandPackage + "." + protocolModel.getClassName().toLowerCase();
		String beanName = beanModel.getName();
		StringBuilder javaImport = new StringBuilder();
		javaImport.append("package ").append(commandPkgOfModule).append(";\n")
				.append("import com.google.protobuf.GeneratedMessage;\n")
				.append("import com.google.protobuf.InvalidProtocolBufferException;\n")
				.append("import com.command.AbstractCommand;\n").append("import com.command.CommandID;\n")
				.append("import com.command.CommandType;\n").append("import com.common.utils.JsonUtil;\n");
		;

		StringBuilder javaClass = new StringBuilder();
		String className = "";
		String requestType = "";
		String paramClassName = beanName;
		if (type == 1) {
			className = StringUtil.firstToUpper(beanName) + "CommandRequest";
			requestType = "CommandType.REQUEST";
			paramClassName += "Request";
		} else {
			className = StringUtil.firstToUpper(beanName) + "CommandResponse";
			requestType = "CommandType.RESPONSE";
			paramClassName += "Response";
		}
		javaClass.append("@CommandID(ID = " + beanModel.getId() + ", type = " + requestType + ")\n");
		javaClass.append("public final class ").append(className).append(" extends AbstractCommand {").append(onetab);

		String protoInfo = "protoInfo";
		if (varModels.size() > 0) {
			String importProtoClass = commandPackage + ".protos." + protocolModel.getClassName() + "Proto";
			javaImport.append("import ").append(importProtoClass)
					.append(".").append(paramClassName).append(";\n");
			javaClass.append("private ").append(paramClassName).append(" ").append(protoInfo).append(";").append(onetab)
					.append(onetab);
		}

		// 构造方法：无参
		javaClass.append("public ").append(className).append("(){}").append(onetab).append(onetab);
		if (varModels.size() > 0) {
			// 带参数的构造方法。
			javaClass.append("public ").append(className).append("(");

			for (VarModel varModel : varModels) {
				String fieldType = getFieldType(varModel.getType());
				if (fieldType == null) {
					fieldType = varModel.getType();
					// 可能是自定义类型。
					javaImport.append("import ").append(commandPkgOfModule).append(".").append(protocolModel.getClassName())
							.append(".").append(varModel.getType()).append(";\n");
				}
				if (varModel.getAttr().equals("repeated")) {
					if (javaImport.indexOf(".List;") == -1) {
						javaImport.append(JavaImport.listImport);
					}
					fieldType = "List<" + fieldType + ">";
				}
				javaClass.append(fieldType).append(" ").append(varModel.getName()).append(",");
			}
			javaClass.delete(javaClass.length() - 1, javaClass.length());
			javaClass.append(") {").append(twotab);
			// 参数赋值
			javaClass.append(paramClassName).append(".Builder builder = ").append(paramClassName)
					.append(".newBuilder();").append(twotab);
			for (VarModel varModel : varModels) {
				String fieldType = getFieldType(varModel.getType());
				boolean baseType = true;
				if (fieldType == null) {
					fieldType = varModel.getType();
					baseType = false;
				}
				if (varModel.getAttr().equals("repeated")) {
					fieldType = "List<" + fieldType + ">";
					javaClass.append("if(").append(varModel.getName()).append(" != null){").append(thirdtab)
							.append("builder").append(".addAll").append(ToolUtil.getClassName(varModel.getName()))
							.append("(").append(varModel.getName()).append(");").append(twotab).append("}")
							.append(twotab);
				} else {
					if (baseType) {
						javaClass.append("builder").append(".set").append(ToolUtil.getClassName(varModel.getName()))
								.append("(").append(varModel.getName()).append(");").append(twotab);
					} else {
						javaClass.append("if(").append(varModel.getName()).append(" != null) {").append(thirdtab)
								.append("builder").append(".set").append(ToolUtil.getClassName(varModel.getName()))
								.append("(").append(varModel.getName()).append(");").append(twotab).append("}")
								.append(twotab);
					}
				}

			}
			javaClass.append("protoInfo = builder.build();").append(onetab);
			javaClass.append("}").append(onetab).append(onetab);
		}
		// 生成Set Get方法

		for (VarModel varModel : varModels) {
			String javaType = getFieldType(varModel.getType());
			if (javaType == null) {
				javaType = varModel.getType();
			}
			if (varModel.getAttr().equals("repeated")) {
				javaType = "List<" + javaType + ">";
				javaClass.append("public ").append(javaType).append(" get")
						.append(ToolUtil.getClassName(varModel.getName())).append("() {").append(twotab)
						.append("return protoInfo.get").append(ToolUtil.getClassName(varModel.getName()))
						.append("List();").append(onetab).append("}").append(onetab);
			} else {
				javaClass.append("public ").append(javaType).append(" get")
						.append(ToolUtil.getClassName(varModel.getName())).append("() {").append(twotab)
						.append("return protoInfo.get").append(ToolUtil.getClassName(varModel.getName())).append("();")
						.append(onetab).append("}").append(onetab);
			}
			javaClass.append(onetab);
		}

		// 生成序列化方法
		javaClass.append("@Override").append(onetab);
		javaClass.append("protected void parseFromBytes(byte[] bytes) throws InvalidProtocolBufferException {")
				.append(twotab);
		if (varModels.size() > 0) {
			javaClass.append("this.").append(protoInfo).append(" = ").append(paramClassName)
					.append(".parseFrom(bytes);");
		}
		javaClass.append(onetab).append("}").append(onetab);
		javaClass.append(onetab);
		javaClass.append("@Override").append(onetab).append("protected GeneratedMessage getGenerateMessage() {");
		javaClass.append(twotab).append("return ");
		if (varModels.size() > 0) {
			javaClass.append(protoInfo);
		} else {
			javaClass.append("null");
		}
		javaClass.append(";").append(onetab).append("}");
		javaClass.append(onetab);
		javaClass.append(onetab);

		// 获取CommandId的方法
		javaClass.append("@Override").append(onetab);
		javaClass.append("public int getCommandId() {").append(twotab);
		javaClass.append("return ").append(beanModel.getId()).append(";").append(onetab).append("}");
		javaClass.append(onetab);
		javaClass.append(onetab);

		// 重写toString方法
		javaClass.append("@Override").append(onetab).append("public String toString() {").append(twotab);
		javaClass.append("StringBuilder str = new StringBuilder();").append(twotab);
		javaClass.append("str.append(\"" + className + "\").append(\"\\n\");").append(twotab);
		String bodyString = "";
		if (varModels.size() > 0) {
			bodyString = ".append(JsonUtil.protoBufToJson(protoInfo))";
		}
		javaClass
				.append("str.append(\"Head:\").append(JsonUtil.objToJson(this.getHead())).append(\"\\n\").append(\"body:\")"
						+ bodyString + ";")
				.append(twotab);
		javaClass.append("return str.toString();").append(onetab).append("}");
		javaClass.append("\n}");

		// 写入到文件，生成java类。
		String path = commandPathOfModule + "/" + className + ".java";
		javaImport.append("\n\n//").append(beanModel.getDesc()).append("\n").append(javaClass);
		writeFile(path, javaImport);
		System.out.println("----生成Java类成功->" + path + "----");
	}

	private static void buildCommandHandler(ProtocolModel protocolModel) {
		for (BeanModel beanModel : protocolModel.getBeanList()) {
			String handlerPath = requestPath + File.separator + protocolModel.getClassName().toLowerCase();
			String handlerPck = requestPackage + "." + protocolModel.getClassName().toLowerCase();
			handlerPck = handlerPck.toLowerCase();
			StringBuilder javaClass = new StringBuilder();
			String className = "Abstract" + beanModel.getName() + "CommandHandler";
			StringBuilder importBuild = new StringBuilder();
			importBuild.append("package ").append(handlerPck).append(";\n\n");
			// 导入固定的引用
			importBuild.append("import com.error.IGameError;\n");
			importBuild.append("import com.gamechannel.GameChannelContext;\n").append("import com.command.Command;\n")
					.append("import com.command.ICommand;\n").append("import com.handler.ICommandHandler;\n");
			String requestClassName = beanModel.getName() + "CommandRequest";
			String responseClassName = beanModel.getName() + "CommandResponse";
			String importCommandPck = commandPackage + "." + protocolModel.getClassName().toLowerCase() ;
			// 导入用到的生成类引用。
			importBuild.append("import ").append(importCommandPck).append(".")
					.append(requestClassName).append(";\n");
			importBuild.append("import ").append(importCommandPck).append(".")
					.append(responseClassName).append(";\n");
			// 加入command注解
			javaClass.append("@Command(").append(requestClassName).append(".class)\n");
			// 类名
			javaClass.append("public abstract class ").append(className).append(" implements ICommandHandler{")
					.append(onetab).append(onetab);
			// 实现各个方法
			// verifyCommand0
			// 记录抽象方法
			StringBuilder abstractVerifyCommand = new StringBuilder();
			abstractVerifyCommand.append("public abstract boolean verifyCommand(");
			String override = "@Override" + onetab;
			javaClass.append(override);
			javaClass.append("public boolean verifyCommand0(ICommand command, GameChannelContext ctx) throws Exception {")
					.append(twotab);
			if (beanModel.getRequestModel().getVarList() != null) {
				javaClass.append(requestClassName).append(" request").append(" = ").append("(" + requestClassName + ")")
						.append("command;").append(twotab);
			}
			javaClass.append("return verifyCommand(");
			// 判断是否有请求参数
			List<VarModel> varModels = beanModel.getRequestModel().getVarList();
			boolean isVar = false;
			if (varModels != null && !varModels.isEmpty()) {
				isVar = true;
				for (VarModel varModel : varModels) {
					String fieldType = getFieldType(varModel.getType());

					if (fieldType == null) {
						fieldType = varModel.getType();
					}
					if (varModel.getAttr().equals("repeated")) {
						fieldType = "List<" + fieldType + ">";
						if (importBuild.indexOf(".List;") == -1) {
							importBuild.append(JavaImport.listImport);
						}
					}
					javaClass.append("request.get").append(ToolUtil.getClassName(varModel.getName())).append("(),");
					abstractVerifyCommand.append(fieldType).append(" ").append(varModel.getName()).append(",");
				}
				// javaClass.delete(javaClass.length() - 1, javaClass.length());
				// abstractVerifyCommand.delete(abstractVerifyCommand.length() -
				// 1, javaClass.length());
			}

			javaClass.append("ctx);").append(onetab).append("}").append(onetab).append(onetab);
			abstractVerifyCommand.append("GameChannelContext ctx) throws Exception;").append(onetab).append(onetab);

			// action0方法
			StringBuilder abstractAction = new StringBuilder();
			abstractAction.append("public abstract boolean action(");
			javaClass.append(override);

			javaClass.append("public boolean action0(ICommand command, GameChannelContext ctx) throws Exception{ ")
					.append(twotab);
			if (beanModel.getRequestModel().getVarList() != null) {
				javaClass.append(requestClassName).append(" request").append(" = ").append("(" + requestClassName + ")")
						.append("command;").append(twotab);
			}
			javaClass.append("return action(");
			if (isVar) {
				for (VarModel varModel : varModels) {
					String fieldType = getFieldType(varModel.getType());
					if (fieldType == null) {
						fieldType = varModel.getType();
					}
					if (varModel.getAttr().equals("repeated")) {
						fieldType = "List<" + fieldType + ">";
					}
					javaClass.append("request.get").append(ToolUtil.getClassName(varModel.getName())).append("(),");
					abstractAction.append(fieldType).append(" ").append(varModel.getName()).append(",");

				}
				// javaClass.delete(javaClass.length() - 1, javaClass.length());
				// abstractAction.delete(abstractAction.length() - 1,
				// javaClass.length());

			}
			javaClass.append("ctx);").append(onetab).append("}").append(onetab).append(onetab);
			abstractAction.append("GameChannelContext ctx) throws Exception;").append(onetab).append(onetab);

			// returnResult0
			StringBuilder abstractReturnResult = new StringBuilder();
			abstractReturnResult.append("public abstract void returnResult(");
			javaClass.append(override);
			javaClass.append("public void returnResult0(ICommand command, GameChannelContext ctx) throws Exception{")
					.append(twotab);
			if (beanModel.getRequestModel().getVarList() != null) {
				javaClass.append(requestClassName).append(" request").append(" = ").append("(" + requestClassName + ")")
						.append("command;").append(twotab);
			}
			javaClass.append("returnResult(");
			if (isVar) {
				for (VarModel varModel : varModels) {
					String fieldType = getFieldType(varModel.getType());
					if (fieldType == null) {
						fieldType = varModel.getType();
					}
					if (varModel.getAttr().equals("repeated")) {
						fieldType = "List<" + fieldType + ">";
					}
					javaClass.append("request.get").append(ToolUtil.getClassName(varModel.getName())).append("(),");
					abstractReturnResult.append(fieldType).append(" ").append(varModel.getName()).append(",");

				}
				// javaClass.delete(javaClass.length() - 1, javaClass.length());
				// abstractReturnResult.delete(abstractReturnResult.length() -
				// 1, javaClass.length());

			}
			javaClass.append(" ctx);").append(onetab).append("}").append(onetab).append(onetab);
			abstractReturnResult.append("GameChannelContext ctx) throws Exception;").append(onetab).append(onetab);

			// sendCommand方法
			javaClass.append("public void sendCommand(");
			varModels = beanModel.getResponseModel().getVarList();
			isVar = false;
			if (varModels != null && !varModels.isEmpty()) {
				isVar = true;
				for (VarModel varModel : varModels) {
					String fieldType = getFieldType(varModel.getType());

					if (fieldType == null) {
						fieldType = varModel.getType();
						// 可能是自定义类型。
						importBuild.append("import ").append(handlerPck).append(".")
								.append(protocolModel.getClassName()).append(".").append(varModel.getType())
								.append(";\n");

					}
					if (varModel.getAttr().equals("repeated")) {
						fieldType = "List<" + fieldType + ">";
						if (importBuild.indexOf(".List;") == -1) {
							importBuild.append(JavaImport.listImport);
						}
					}
					javaClass.append(fieldType).append(" ").append(varModel.getName()).append(",");
				}
				// javaClass.delete(javaClass.length() - 1, javaClass.length());
			}
			javaClass.append("GameChannelContext ctx){").append(twotab);
			javaClass.append(responseClassName).append(" response = new ").append(responseClassName).append("(");
			if (isVar) {
				for (VarModel varModel : varModels) {
					javaClass.append(varModel.getName()).append(",");
				}
				javaClass.delete(javaClass.length() - 1, javaClass.length());
			}
			javaClass.append(");").append(twotab);
			javaClass.append("ctx.sendCommand(response);").append(onetab).append("}").append(onetab).append(onetab);
			//sendError
			
			javaClass.append("protected void sendError(IGameError error,GameChannelContext ctx){").append(twotab)
			.append("ChatCommandResponse response = new ChatCommandResponse();").append(twotab)
				.append("ctx.sendCommand(response,error);").append(onetab).append("}").append(onetab);
			
			
			// 加入它们三个的抽象方法
			javaClass.append(abstractVerifyCommand).append(abstractAction).append(abstractReturnResult);
			javaClass.append("\n}");
			importBuild.append("\n\n").append(javaClass);

			String path = handlerPath + "/" + className + ".java";
			writeFile(path, importBuild);
			System.out.println("----生成CommandHandler类成功->" + path);
		}

	}

	private static void writeFile(String pathFile, StringBuilder msg) {
		File file = new File(pathFile);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(msg.toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFieldType(String type) {
		String javaType = null;
		switch (type) {
		case "sint32":
		case "int32":
			javaType = "Integer";
			break;
		case "sint64":
		case "int64":
			javaType = "Long";
			break;
		case "string":
			javaType = "String";
			break;
		case "bool":
			javaType = "boolean";
			break;
		case "bytes":
			javaType = "byte[]";
			break;
		default:
			if (type.equals("int")) {
				throw new IllegalArgumentException("协议配置文件中不能使用int，请填写int32或int64");
			}
			if (type.equals("long")) {
				throw new IllegalArgumentException("协议配置文件中不能使用long,请使用int64代替");
			}
			break;
		}
		return javaType;
	}
}
