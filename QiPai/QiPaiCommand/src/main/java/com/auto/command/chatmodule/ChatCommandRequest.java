package com.auto.command.chatmodule;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.command.AbstractCommand;
import com.command.CommandID;
import com.command.CommandType;
import com.common.utils.JsonUtil;
import com.auto.command.protos.ChatModuleProto.ChatRequest;


//聊天协议
@CommandID(ID = 1012, type = CommandType.REQUEST)
public final class ChatCommandRequest extends AbstractCommand {
	private ChatRequest protoInfo;
	
	public ChatCommandRequest(){}
	
	public ChatCommandRequest(String content) {
		ChatRequest.Builder builder = ChatRequest.newBuilder();
		builder.setContent(content);
		protoInfo = builder.build();
	}
	
	public String getContent() {
		return protoInfo.getContent();
	}
	
	@Override
	protected void parseFromBytes(byte[] bytes) throws InvalidProtocolBufferException {
		this.protoInfo = ChatRequest.parseFrom(bytes);
	}
	
	@Override
	protected GeneratedMessage getGenerateMessage() {
		return protoInfo;
	}
	
	@Override
	public int getCommandId() {
		return 1012;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("ChatCommandRequest").append("\n");
		str.append("Head:").append(JsonUtil.objToJson(this.getHead())).append("\n").append("body:").append(JsonUtil.protoBufToJson(protoInfo));
		return str.toString();
	}
}