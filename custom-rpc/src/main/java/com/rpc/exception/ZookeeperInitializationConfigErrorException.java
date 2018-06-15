package com.rpc.exception;

public class ZookeeperInitializationConfigErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public ZookeeperInitializationConfigErrorException() {
		super();
	}
	
	public ZookeeperInitializationConfigErrorException(String s) {
		super(s);
	}
}
