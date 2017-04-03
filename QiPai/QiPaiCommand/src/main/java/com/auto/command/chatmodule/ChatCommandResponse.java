package com.auto.command.chatmodule;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.command.AbstractCommand;
import com.command.CommandID;
import com.command.CommandType;
import com.common.utils.JsonUtil;
import com.auto.command.protos.ChatModuleProto.ChatResponse;


//聊天协议
@CommandID(ID = 1012, type = CommandType.RESPONSE)
public final class ChatCommandResponse extends AbstractCommand {
	private ChatResponse protoInfo;
	
	public ChatCommandResponse(){}
	
	public ChatCommandResponse(String content,Integer pos) {
		ChatResponse.Builder builder = ChatResponse.newBuilder();
		builder.setContent(content);
		builder.setPos(pos);
		protoInfo = builder.build();
	}
	
	public String getContent() {
		return protoInfo.getContent();
	}
	
	public Integer getPos() {
		return protoInfo.getPos();
	}
	
	@Override
	protected void parseFromBytes(byte[] bytes) throws InvalidProtocolBufferException {
		this.protoInfo = ChatResponse.parseFrom(bytes);
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
		str.append("ChatCommandResponse").append("\n");
		str.append("Head:").append(JsonUtil.objToJson(this.getHead())).append("\n").append("body:").append(JsonUtil.protoBufToJson(protoInfo));
		return str.toString();
	}
}