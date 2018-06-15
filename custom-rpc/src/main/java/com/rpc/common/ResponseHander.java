package com.rpc.common;

import com.rpc.client.RpcClient;
import com.rpc.exception.ResponseTimeOutException;
import com.rpc.message.RpcResponse;

public class ResponseHander {

	private RpcResponse rpcResponse;

	private Long timeStamp;

	private Boolean isOverTime = Boolean.FALSE;
	
	public RpcResponse getRpcResponse(String messageId,RpcClient rpcClient) throws Exception {
		this.timeStamp = System.currentTimeMillis();
		synchronized (this) {
			wait(rpcClient.getConfig().getConnectionTimeOut());
		}
		if (!isOverTime
				&& ((System.currentTimeMillis() - this.timeStamp)) >= rpcClient.getConfig().getConnectionTimeOut()) {
			rpcClient.responseHanderMappingRemove(messageId);
			throw new ResponseTimeOutException();
		}
		return rpcResponse;
	}

	public void setRpcResponse(RpcResponse rpcResponse) {
		this.rpcResponse = rpcResponse;
		this.isOverTime = Boolean.TRUE;
		synchronized (this) {
			notify();
		}
	}

}
