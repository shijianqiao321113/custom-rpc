package com.rpc.message;

import java.util.Arrays;


public class RpcRequest {

	private MessageHead messageHead;

	private String className;
	
	private String methodName;
	
	private Class<?>[] parameterTypes;
	
	private Object[] parameters;
	
	public MessageHead getMessageHead() {
		return messageHead;
	}

	public void setMessageHead(MessageHead messageHead) {
		this.messageHead = messageHead;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "RpcRequest [messageHead=" + messageHead + ", className="
				+ className + ", methodName=" + methodName
				+ ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", parameters=" + Arrays.toString(parameters) + "]";
	}

	
}
