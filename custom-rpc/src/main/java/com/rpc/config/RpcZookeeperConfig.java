package com.rpc.config;

import com.rpc.tools.Constant;


public class RpcZookeeperConfig {

	private String networkIP;
	
	private String connectString;
	
	private String serverName;
	
	private int sessionTimeout = Constant._10000;
	
	private int connectionTimeout = Constant._10000;
	
	public RpcZookeeperConfig(){
		
	}
	
	public RpcZookeeperConfig(String connectString,
			String serverName) {
		this(null,connectString,serverName);
	}
	
	public RpcZookeeperConfig(String networkIP, String connectString,
			String serverName) {
		this(networkIP,connectString,serverName,Constant._10000);
	}
	
	public RpcZookeeperConfig(String networkIP, String connectString,
			String serverName, int sessionTimeout) {
		this(networkIP,connectString,serverName,sessionTimeout,Constant._10000);
	}
	
	public RpcZookeeperConfig(String networkIP, String connectString,
			String serverName, int sessionTimeout,int connectionTimeout) {
		this.networkIP = networkIP;
		this.connectString = connectString;
		this.serverName = serverName;
		this.sessionTimeout = sessionTimeout;
		this.connectionTimeout = connectionTimeout;
	}

	public String getNetworkIP() {
		return networkIP;
	}

	public RpcZookeeperConfig setNetworkIP(String networkIP) {
		this.networkIP = networkIP;
		return this.self();
	}

	public String getConnectString() {
		return connectString;
	}

	public RpcZookeeperConfig setConnectString(String connectString) {
		this.connectString = connectString;
		return this.self();
	}

	public String getServerName() {
		return serverName;
	}

	public RpcZookeeperConfig setServerName(String serverName) {
		this.serverName = serverName;
		return this.self();
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public RpcZookeeperConfig setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
		return this.self();
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public RpcZookeeperConfig setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this.self();
	}
	
	private RpcZookeeperConfig self(){
		return this;
	}
}
