package com.rpc.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.rpc.common.NodeData;
import com.rpc.exception.NodeNotExistException;
import com.rpc.tools.Constant;

class ZookeeperClient{
	
	private RpcClient rpcClient;
	
	private ZkClient zk;
	
	public ZookeeperClient(RpcClient rpcClient){
		
		this.rpcClient = rpcClient;
		
		try {
			if(Constant.isNullOrEmpty(this.rpcClient.getZookeeperConfig().getNetworkIP())){
				this.rpcClient.getZookeeperConfig().setNetworkIP(InetAddress.getLocalHost().getHostAddress());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			zk = new ZkClient(this.rpcClient.getZookeeperConfig().getConnectString(),
					this.rpcClient.getZookeeperConfig().getSessionTimeout(),
					this.rpcClient.getZookeeperConfig().getConnectionTimeout());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		zk.subscribeStateChanges(new IZkStateListener() {
			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				if(KeeperState.Disconnected == state){}
				if(KeeperState.SyncConnected == state){
					exec();
				}
			}
			@Override
			public void handleSessionEstablishmentError(Throwable error)
					throws Exception {
				error.printStackTrace();
			}
			@Override
			public void handleNewSession() throws Exception {
				exec();
			}
		});
		exec();
	}
	
	private void exec(){
		
		tcpServiceLoad();
		strategyConfigLoad();
		weightConfigLoad();
		apiServiceMappingLoad();
		registerConsumerNode();
		
		/*服务提供者节点下的子节点变更监控*/
		zk.subscribeChildChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER), new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				tcpServiceLoad();
			}
		});
		/*负载策略配置变更监控*/
		zk.subscribeDataChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.STRATEGY), new IZkDataListener() {
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				strategyConfigLoad();
			}
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				strategyConfigLoad();
			}
		});
		/*服务提供者权重变更监控*/
		zk.subscribeChildChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT), new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				weightConfigLoad();
			}
		});
		/*api节点变更监控*/
		zk.subscribeChildChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API), new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds)
					throws Exception {
				apiServiceMappingLoad();
			}
		});
	}
	
	/*注册消费者节点*/
	private void registerConsumerNode(){
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER))){
			zk.subscribeDataChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER), new IZkDataListener() {
				@Override
				public void handleDataDeleted(String dataPath) throws Exception {}
				
				@Override
				public void handleDataChange(String dataPath, Object data) throws Exception {
					if(!zk.exists(Constant.join(Constant.BACKSLASH,rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER,Constant.BACKSLASH,rpcClient.getZookeeperConfig().getNetworkIP()))){
						zk.create(Constant.join(Constant.BACKSLASH,rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER,Constant.BACKSLASH,rpcClient.getZookeeperConfig().getNetworkIP()), null, CreateMode.EPHEMERAL);
					}
				}
			});
		}else{
			if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER,Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getNetworkIP()))){
				zk.create(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER,Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getNetworkIP()), null, CreateMode.EPHEMERAL);
			}
		}
	}
	
	/*服务提供者链路链接获取*/
	private void tcpServiceLoad(){
		try {
			if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER))){
				throw new NodeNotExistException("no zookeeper service producer node");
			}
			List<String> currentServiceList = zk.getChildren(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER));
			if(currentServiceList.size() <= Constant._0){
				throw new NodeNotExistException("no service node resources under zookeeper service nodes");
			}
			this.rpcClient.updateServerList(currentServiceList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*负载配置获取*/
	private void strategyConfigLoad(){
		try {
			if(zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.STRATEGY))){
				NodeData nodeData = (NodeData)zk.readData(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.STRATEGY));
				if(nodeData != null && nodeData.getStrategyEnum() != null){
					this.rpcClient.setStrategyEnum(nodeData.getStrategyEnum());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*服务提供者权重获取*/
	private void weightConfigLoad(){
		try {
			if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT))){
				return ;
			}
			List<String> nodeList = zk.getChildren(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT));
			Map<String,Integer> serviceWeightConfigMapParmeter = new HashMap<String,Integer>();
			if(nodeList != null){
				for(String node:nodeList){
					NodeData nodeData = (NodeData)zk.readData(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT,Constant.BACKSLASH,node));
					if(nodeData != null){
						serviceWeightConfigMapParmeter.put(node, nodeData.getWeight());
					}
				}
			}
			this.rpcClient.serviceWeightConfigMapPutAll(serviceWeightConfigMapParmeter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*api所有服务提供者获取*/
	private void apiServiceMappingLoad(){
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API))){
			return ;
		}
		List<String> nodeList = zk.getChildren(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API));
		Map<String,Vector<String>> apiServiceMappingMapParmeter = new HashMap<String,Vector<String>>();
		if(nodeList != null){
			for(String node:nodeList){
				List<String> serviceNodeList = zk.getChildren(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,node));
				Vector<String> serviceVector = new Vector<String>(serviceNodeList.size());
				serviceVector.addAll(serviceNodeList);
				apiServiceMappingMapParmeter.put(node,serviceVector);
				zk.subscribeChildChanges(Constant.join(Constant.BACKSLASH,this.rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,node), new IZkChildListener() {
					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds)
							throws Exception {
						Vector<String> serviceList = new Vector<String>(currentChilds.size());
						serviceList.addAll(currentChilds);
						rpcClient.apiServiceMappingMapPut(parentPath.substring(Constant.join(Constant.BACKSLASH,rpcClient.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API).length()+Constant._1), serviceList);
					}
				});
			}
		}
		this.rpcClient.apiServiceMappingMapPutAll(apiServiceMappingMapParmeter);
	}
}