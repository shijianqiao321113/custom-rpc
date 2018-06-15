package com.rpc.codec;

import com.rpc.message.RpcRequest;
import com.rpc.message.RpcResponse;
import com.rpc.tools.SerializeTool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncode extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		if(msg instanceof RpcRequest){
			RpcRequest request = (RpcRequest)msg;
			byte[] data = SerializeTool.serialize(request, RpcRequest.class);
			out.writeByte(request.getMessageHead().getPackageType());
			out.writeByte(request.getMessageHead().getType());
			out.writeInt(data.length);
			out.writeLong(request.getMessageHead().getMessageId());
			out.writeBytes(data);
		}
		if(msg instanceof RpcResponse){
			RpcResponse response = (RpcResponse)msg;
			byte[] data = SerializeTool.serialize(response, RpcResponse.class);
			out.writeByte(response.getMessageHead().getPackageType());
			out.writeByte(response.getMessageHead().getType());
			out.writeInt(data.length);
			out.writeLong(response.getMessageHead().getMessageId());
			out.writeBytes(data);
		}
	}

}
