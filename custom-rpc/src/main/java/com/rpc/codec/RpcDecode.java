package com.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import com.rpc.message.MessageHead;
import com.rpc.message.RpcRequest;
import com.rpc.message.RpcResponse;
import com.rpc.tools.Constant;
import com.rpc.tools.SerializeTool;

public class RpcDecode extends MessageToMessageDecoder<ByteBuf>  {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        
    	byte packageType = in.readByte();
        byte type = in.readByte();
        int contentLength = in.readInt();
        long messageId = in.readLong();
        MessageHead head = new MessageHead(packageType,type,contentLength,messageId);
        
	    byte[] data = new byte[contentLength];
	    if(contentLength > Constant._0){
	    	in.readBytes(data);
	    }
	    switch(type){
	        case Constant._0XAB:
	        	RpcRequest request = SerializeTool.deSerialize(data, RpcRequest.class);
	        	request.setMessageHead(head);
	        	out.add(request);
	        	break;
	        case Constant._0XBC:
	        	RpcResponse response = SerializeTool.deSerialize(data, RpcResponse.class);
	        	response.setMessageHead(head);
	        	out.add(response);
	        	break;
	        default:
	        	return;
	    }
    }

}
