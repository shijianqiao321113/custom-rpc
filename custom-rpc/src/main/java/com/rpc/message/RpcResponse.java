package com.rpc.message;

public class RpcResponse {

	private MessageHead messageHead;

	private Exception exception;
	
	private Object resultData;
	
	public MessageHead getMessageHead() {
		return messageHead;
	}

	public void setMessageHead(MessageHead messageHead) {
		this.messageHead = messageHead;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Object getResultData() {
		return resultData;
	}

	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

	@Override
	public String toString() {
		return "RpcResponse [messageHead=" + messageHead + ", exception="
				+ exception + ", resultData=" + resultData + "]";
	}
	
	

}
