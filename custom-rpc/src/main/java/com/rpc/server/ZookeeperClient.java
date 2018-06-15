package com.rpc.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.rpc.common.NodeData;
import com.rpc.common.StrategyEnum;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.tools.Constant;

class ZookeeperClient{
	
	private RpcServer rpcServer;
	
	private ZkClient zk;
	
	public ZookeeperClient(RpcServer rpcServer){
		this.rpcServer = rpcServer;
	}
	
	public void start() throws ZookeeperInitializationConfigErrorException {
		if(this.rpcServer.getZookeeperConfig() == null || 
				Constant.isNullOrEmpty(this.rpcServer.getZookeeperConfig().getConnectString()) || 
				Constant.isNullOrEmpty(this.rpcServer.getZookeeperConfig().getServerName())){
			return ;
		}
		try {
			if(Constant.isNullOrEmpty(this.rpcServer.getZookeeperConfig().getNetworkIP())){
				this.rpcServer.getZookeeperConfig().setNetworkIP(InetAddress.getLocalHost().getHostAddress());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			zk = new ZkClient(this.rpcServer.getZookeeperConfig().getConnectString(),
					this.rpcServer.getZookeeperConfig().getSessionTimeout(),
					this.rpcServer.getZookeeperConfig().getConnectionTimeout());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		zk.subscribeStateChanges(new IZkStateListener() {
			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				if(KeeperState.Disconnected == state){
				}
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
		
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//				getLockHander();
//			}
//		}, Constant._60000, Constant._60000);
	}
	private void exec(){
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName()))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName()), null, CreateMode.PERSISTENT);
		}
		/*生产者节点*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER), null, CreateMode.PERSISTENT);
		}
		/*生产者，服务IP:端口，临时*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER,Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort()))){
			zk.create(Constant.join(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.PRODUCER,Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort())), null, CreateMode.EPHEMERAL);
		}
		/*消费者节点*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONSUMER), null, CreateMode.PERSISTENT);
		}
		/*api节点*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API), null, CreateMode.PERSISTENT);
		}
		/*api 明细信息*/
		for(Map.Entry<String, Class<?>> m:this.rpcServer.getServiceMappingMap().entrySet()){
			if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,m.getKey()))){
				zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,m.getKey()),null, CreateMode.PERSISTENT);
			}
			/*提供当前api接口的机器，临时*/
			if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,m.getKey(),Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort()))){
				zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.API,Constant.BACKSLASH,m.getKey(),Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort()), null, CreateMode.EPHEMERAL);
			}
		}
		/*lock 锁节点*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK), null, CreateMode.PERSISTENT);
		}
		/*全局配置*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG), null, CreateMode.PERSISTENT);
		}
		/*负载策略配置*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.STRATEGY))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.STRATEGY), new NodeData().setStrategyEnum(StrategyEnum.RANDOM), CreateMode.PERSISTENT);
		}
		/*负载权重根配置*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT), null, CreateMode.PERSISTENT);
		}
		/*负载权重子节点初始化*/
		if(!zk.exists(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT,Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort()))){
			zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.CONFIG,Constant.BACKSLASH,Constant.WEIGHT,Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getNetworkIP(),Constant.COLON,this.rpcServer.getPort()), new NodeData().setWeight(Constant._1), CreateMode.PERSISTENT);
		}
	}
	
//	public void getLockHander(){
//		checkMinPath(zk.create(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK,Constant.BACKSLASH,Constant.SUB),null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL));	
//		return ;
//	}
//	private void checkMinPath(String selfPath){
//		List<String> subNodes = zk.getChildren(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK));  
//        Collections.sort(subNodes);  
//        int index = subNodes.indexOf(selfPath.substring(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK).length()+Constant._1));  
//        switch (index){
//        case Constant.__1: 
//        	 return ;  
//        case Constant._0:  
//            getLockSuccess(selfPath);
//            return ;
//        default:
//            zk.subscribeDataChanges(Constant.join(Constant.BACKSLASH,this.rpcServer.getZookeeperConfig().getServerName(),Constant.BACKSLASH,Constant.LOCK,Constant.BACKSLASH,subNodes.get(index - Constant._1)), new IZkDataListener() {
//				@Override
//				public void handleDataDeleted(String dataPath) throws Exception {
//					checkMinPath(selfPath);
//				}
//				@Override
//				public void handleDataChange(String dataPath, Object data) throws Exception {
//					checkMinPath(selfPath);
//				}
//			});
//        }  
//	}
//	
//	private void getLockSuccess(String selfPath){
//		/*修改节点信息*/
//		/**
//		 * TODO
//		 */
//	}
}