package com.rpc.exception;

public class LinkToServerErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public LinkToServerErrorException() {
		super();
	}
	
	public LinkToServerErrorException(String s) {
		super(s);
	}
}
