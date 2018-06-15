package com.rpc.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.rpc.algorithm.LoadAlgorithm;
import com.rpc.algorithm.impl.IpHashAlgorithmImpl;
import com.rpc.algorithm.impl.PollingAlgorithmImpl;
import com.rpc.algorithm.impl.RandomAlgorithmImpl;
import com.rpc.common.ResponseHander;
import com.rpc.common.StrategyEnum;
import com.rpc.config.RpcClientConfig;
import com.rpc.config.RpcZookeeperConfig;
import com.rpc.exception.ApiInterfaceLaunchCallException;
import com.rpc.exception.NoServiceRresourcesException;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.message.RpcRequest;
import com.rpc.tools.Constant;

public class RpcClient {
	
	private static final Map<String,RpcClient> clientMap = new HashMap<String, RpcClient>();
	
	private StrategyEnum strategyEnum = StrategyEnum.RANDOM;
	
	private int currentSelectServiceIndex = Constant._0;
	
	private Boolean isCluster;
	
	private final Map<String,Integer> serviceWeightConfigMap = new ConcurrentHashMap<String,Integer>(); 
	
	private final Map<String,Vector<String>> apiServiceMappingMap = new ConcurrentHashMap<String,Vector<String>>();  
	
	private Map<String, ResponseHander> responseHanderMapping = new ConcurrentHashMap<String, ResponseHander>();
	
	private Map<String, TcpClient> serverMap = new ConcurrentHashMap<String, TcpClient>();

	private ZookeeperClient zookeeperClient;
	
	private InetSocketAddress address;
	
	private RpcClientConfig config;

	private Executor exec;
	
	private RpcZookeeperConfig zookeeperConfig;
	
	public static RpcClient getRpcClient(InetSocketAddress address,RpcClientConfig config,Executor exec){
		synchronized (clientMap) {
			if(!clientMap.containsKey(Constant.join(address.getHostName(),Constant.UNDERLINE,address.getPort()))){
				clientMap.put(Constant.join(address.getHostName(),Constant.UNDERLINE,address.getPort()), new RpcClient(address,config,exec,null));
			}
			return clientMap.get(Constant.join(address.getHostName(),Constant.UNDERLINE,address.getPort()));
		}
	}
	
	public static RpcClient getRpcClient(RpcClientConfig config,Executor exec,RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		if(zookeeperConfig == null || 
				Constant.isNullOrEmpty(zookeeperConfig.getConnectString()) || 
				Constant.isNullOrEmpty(zookeeperConfig.getServerName())){
			throw new ZookeeperInitializationConfigErrorException();
		}
		synchronized (clientMap) {
			if(!clientMap.containsKey(Constant.join(zookeeperConfig.getConnectString(),Constant.UNDERLINE,zookeeperConfig.getServerName()))){
				clientMap.put(Constant.join(zookeeperConfig.getConnectString(),Constant.UNDERLINE,zookeeperConfig.getServerName()), new RpcClient(null,config,exec,zookeeperConfig));
			}
			return clientMap.get(Constant.join(zookeeperConfig.getConnectString(),Constant.UNDERLINE,zookeeperConfig.getServerName()));
		}
	}
	
	private RpcClient(InetSocketAddress address,RpcClientConfig config,Executor exec,RpcZookeeperConfig zookeeperConfig){
		if(exec == null){
			exec = (Executor) Executors.newFixedThreadPool(Constant._8); 
		}
		if(config == null){
			config = new RpcClientConfig();
		}
		this.address = address;
		this.config = config;
		this.exec = exec;
		this.zookeeperConfig = zookeeperConfig;
		
		if(this.address != null){
			this.isCluster = Boolean.FALSE;
			TcpClient tcpClient = new TcpClient(this,this.address,this.isCluster);
			this.addNewService(Constant.join(this.address.getHostName(),Constant.COLON,this.address.getPort()),tcpClient);
			tcpClient.start();
		}
		if(zookeeperConfig != null){
			this.isCluster = Boolean.TRUE;
			this.zookeeperClient = new ZookeeperClient(this);
		}
	}
	
	public TcpClient getAvailableTcpClient(RpcRequest request) throws NoServiceRresourcesException, ApiInterfaceLaunchCallException{
		if(this.serverMap.size() <= Constant._0){
			throw new NoServiceRresourcesException("no service connection resources");
		}
		/*点对点调用,获取tcpClient*/
		if(!this.isCluster){
			return this.serverMap.entrySet().iterator().next().getValue();
		}
		/*集群调用,获取经过算法后的tcpClient*/
		if(!this.apiServiceMappingMap.containsKey(request.getClassName())){
			throw new ApiInterfaceLaunchCallException("no api interface");
		}
		Vector<String> serviceList = this.apiServiceMappingMap.get(request.getClassName());
		if(serviceList.size() <= Constant._0){
			throw new NoServiceRresourcesException("current api no service provider");
		}
		if(serviceList.size() == Constant._1){
			if(!this.serverMap.containsKey(serviceList.get(Constant._0))){
				throw new NoServiceRresourcesException("not linked to this api interface service provider");
			}
			return this.serverMap.get(serviceList.get(Constant._0));
		}
		
		Map<String,Integer> weightMap = new HashMap<String,Integer>();
		weightMap.putAll(this.serviceWeightConfigMap);
		
		String selectService = this.getSelectAlgorithm().selector(serviceList,weightMap);
		if(!Constant.isNullOrEmpty(selectService) && this.serverMap.containsKey(selectService)){
			return this.serverMap.get(selectService);
		}
		/*允许一次选择服务节点失败，重新选择服务节点*/
		serviceList.remove(selectService);
		if(serviceList.size() == Constant._1){
			return this.serverMap.get(serviceList.get(Constant._0));
		}
		return this.serverMap.get(this.getSelectAlgorithm().selector(serviceList,weightMap));
	}
	
	/* 获取算法 */
	private LoadAlgorithm getSelectAlgorithm() {
		switch (this.strategyEnum) {
		case RANDOM:
			return new RandomAlgorithmImpl();
		case POLLING:
			return new PollingAlgorithmImpl(this);
		case IP_HASH:
			return new IpHashAlgorithmImpl(this.zookeeperConfig.getNetworkIP());
		default:
			return new RandomAlgorithmImpl();
		}
	}
	
	public ZookeeperClient getZookeeperClient() {
		return zookeeperClient;
	}
	public InetSocketAddress getAddress() {
		return address;
	}
	public RpcClientConfig getConfig() {
		return config;
	}
	public Executor getExec() {
		return exec;
	}
	public RpcZookeeperConfig getZookeeperConfig() {
		return zookeeperConfig;
	}
	public ResponseHander responseHanderMappingRemove(String messageId){
		return this.responseHanderMapping.remove(messageId);
	}
	public void setResponseHanderMapping(String messageId, ResponseHander responseHander) {
		this.responseHanderMapping.put(messageId, responseHander);
	}
	public void removeServer(String ip_port){
		this.serverMap.remove(ip_port);
	}
	public void addNewService(String ip_port,TcpClient tcpClient){
		this.serverMap.put(ip_port, tcpClient);
	}
	public void updateServerList(List<String> currentServiceList){
		for(String service:currentServiceList){
			if(!this.serverMap.containsKey(service)){
				TcpClient tcpClient = new TcpClient(this,new InetSocketAddress(service.split(Constant.COLON)[0],Integer.parseInt(service.split(Constant.COLON)[1])),this.isCluster);
				this.addNewService(service, tcpClient);
				tcpClient.start();
			}
		}
	}	

	public StrategyEnum getStrategyEnum() {
		return this.strategyEnum;
	}
	public void setStrategyEnum(StrategyEnum strategyEnum) {
		this.strategyEnum = strategyEnum;
	}
	
	public void serviceWeightConfigMapPutAll(Map<String,Integer> serviceWeightConfigMapParmeter){
		Iterator<Map.Entry<String,Integer>> it = this.serviceWeightConfigMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<String,Integer> entry = it.next();
            if(!serviceWeightConfigMapParmeter.containsKey(entry.getKey())){
            	it.remove();
            }
        }
        this.serviceWeightConfigMap.putAll(serviceWeightConfigMapParmeter);
	}
	
	public void apiServiceMappingMapPutAll(Map<String,Vector<String>> apiServiceMappingMapParmeter){
		Iterator<Map.Entry<String,Vector<String>>> it = this.apiServiceMappingMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,Vector<String>> entry = it.next();
			if(!apiServiceMappingMapParmeter.containsKey(entry.getKey())){
				it.remove();
			}
		}
		this.apiServiceMappingMap.putAll(apiServiceMappingMapParmeter);
	}
	
	public void apiServiceMappingMapPut(String api,Vector<String> serviceList){
		this.apiServiceMappingMap.put(api, serviceList);
	}

	public int getCurrentSelectServiceIndex() {
		return currentSelectServiceIndex;
	}

	public void setCurrentSelectServiceIndex(int currentSelectServiceIndex) {
		this.currentSelectServiceIndex = currentSelectServiceIndex;
	}
	
}



