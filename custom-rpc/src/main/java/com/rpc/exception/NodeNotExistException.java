package com.rpc.exception;

public class NodeNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public NodeNotExistException() {
		super();
	}
	
	public NodeNotExistException(String s) {
		super(s);
	}
}
