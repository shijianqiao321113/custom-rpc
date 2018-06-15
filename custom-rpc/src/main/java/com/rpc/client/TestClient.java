package com.rpc.client;

import com.rpc.config.RpcZookeeperConfig;
import com.rpc.exception.NodeNotExistException;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.proxy.RpcProxy;
import com.rpc.testApi.Api;

public class TestClient {

	public static void main(String[] args) throws ZookeeperInitializationConfigErrorException, NodeNotExistException {
		
		/*点对点测试*/
		/*RpcProxy proxy = new RpcProxy("127.0.01",8080);*/
		
		/*zookeeper 集群测试*/
		final RpcProxy proxy  = new RpcProxy(new RpcZookeeperConfig("127.0.0.1:2181","custom_rpc_1"));
		
		for(int i=0;i<1;i++){
			Thread r = new Thread(){
				@Override
				public void run() {
					while(true){
						try {
							Api api = proxy.get(Api.class);
							String str = "test1"+Math.random();
							System.out.println(str+"======"+this.getName()+"========"+api.getRandom(str));
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(0);
						}
						try { Thread.sleep(10); } catch (Exception e) { }
					}	
				}
			};
			r.start();
		}

	}

}
