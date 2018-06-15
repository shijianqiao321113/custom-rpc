package com.rpc.server;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.rpc.config.RpcServerConfig;
import com.rpc.config.RpcZookeeperConfig;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.tools.Constant;

public class RpcServer{
	
	private static final Map<String,RpcServer> serverMap = new HashMap<String,RpcServer>();
	
	private int port;
	private RpcServerConfig config;
	private Executor exec;
	private RpcZookeeperConfig zookeeperConfig;
	private LinkedHashMap<String,Class<?>> serviceMappingMap;
	private TcpServer tcpServer;
	private ZookeeperClient zookeeperClient;
	
	public static RpcServer start(final int port,final RpcServerConfig config){
		try {
			return start(port,config,null,null);
		} catch (ZookeeperInitializationConfigErrorException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static RpcServer start(final int port,final RpcServerConfig config,final Executor exec,final RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		synchronized (serverMap) {
			if(!serverMap.containsKey(String.valueOf(port))){
				serverMap.put(String.valueOf(port), new RpcServer(port, config,exec, zookeeperConfig));
			}
			return serverMap.get(String.valueOf(port));
		}
	}
	
	private RpcServer(int port,RpcServerConfig config,Executor exec,RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		if(exec == null){
			exec = (Executor) Executors.newFixedThreadPool(Constant._3); 
		}
		this.port = port;
		this.config = config;
		this.exec = exec;
		this.zookeeperConfig = zookeeperConfig;
		this.serviceMappingMap = config.initServiceMappingConfig();
		
		this.zookeeperClient = new ZookeeperClient(this);
		this.zookeeperClient.start();
		
		this.tcpServer = new TcpServer(this);
		exec.execute(this.tcpServer);
	}
	public int getPort() {
		return port;
	}
	public RpcServerConfig getConfig() {
		return config;
	}
	public Executor getExec() {
		return exec;
	}
	public RpcZookeeperConfig getZookeeperConfig() {
		return zookeeperConfig;
	}
	public static Map<String, RpcServer> getServermap() {
		return serverMap;
	}
	public LinkedHashMap<String, Class<?>> getServiceMappingMap() {
		return serviceMappingMap;
	}
	public TcpServer getTcpServer() {
		return tcpServer;
	}
	public ZookeeperClient getZookeeperClient() {
		return zookeeperClient;
	}
	
}
