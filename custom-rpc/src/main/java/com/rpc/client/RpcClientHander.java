package com.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import com.rpc.message.RpcResponse;
import com.rpc.tools.Constant;


public class RpcClientHander extends SimpleChannelInboundHandler<RpcResponse> {
	
	private RpcClient rpcClient;
	
	public RpcClientHander(RpcClient rpcClient){
		this.rpcClient = rpcClient;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse)
			throws Exception {
		if(rpcResponse.getMessageHead().getPackageType() == Constant._0XAB){
			return ;
		}
		this.rpcClient.responseHanderMappingRemove(String.valueOf(rpcResponse.getMessageHead().getMessageId())).setRpcResponse(rpcResponse);
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
