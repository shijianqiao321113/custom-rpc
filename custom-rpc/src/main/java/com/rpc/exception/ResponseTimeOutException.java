package com.rpc.exception;

public class ResponseTimeOutException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResponseTimeOutException() {
		super();
	}
	
	public ResponseTimeOutException(String s) {
		super(s);
	}
}
