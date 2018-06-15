package com.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import com.rpc.message.MessageHead;
import com.rpc.message.RpcRequest;
import com.rpc.message.RpcResponse;
import com.rpc.tools.Constant;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
	
	private RpcServer rpcServer;

	public RpcServerHandler(RpcServer rpcServer){
		this.rpcServer = rpcServer;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request)
			throws Exception {
		MessageHead responseMessageHead = new MessageHead(request.getMessageHead().getPackageType(), Constant._0XBC,null, request.getMessageHead().getMessageId());
		RpcResponse response = new RpcResponse();
		response.setMessageHead(responseMessageHead);
		if(request.getMessageHead().getPackageType() == Constant._0XAB){
			ctx.writeAndFlush(response);
			return ;
		}
        try {
            Class clazz = this.rpcServer.getServiceMappingMap().get(request.getClassName());
            response.setResultData(clazz.getMethod(request.getMethodName(), request.getParameterTypes()).invoke(clazz.newInstance(), request.getParameters()));
		} catch (Exception e) {
			response.setException(e);
		}
        ctx.writeAndFlush(response);
		return ;
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent e = (IdleStateEvent)evt;
			if(e.state() == IdleState.READER_IDLE){
				ctx.close();
			}
			if(e.state() == IdleState.WRITER_IDLE){
				ctx.close();
			}
			if(e.state() == IdleState.ALL_IDLE){
				ctx.close();
			}
		}
	}
	
}
