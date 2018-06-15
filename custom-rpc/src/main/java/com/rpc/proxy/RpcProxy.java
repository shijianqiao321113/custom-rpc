package com.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.rpc.client.RpcClient;
import com.rpc.config.RpcClientConfig;
import com.rpc.config.RpcZookeeperConfig;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.message.MessageHead;
import com.rpc.message.RpcRequest;
import com.rpc.message.RpcResponse;
import com.rpc.tools.Constant;
import com.rpc.tools.Counter;

public class RpcProxy implements InvocationHandler {
	
	private InetSocketAddress address;
	
	private RpcClientConfig config;

	private Executor exec;
	
	private RpcZookeeperConfig zookeeperConfig;
	
	private RpcClient rpcClient;
	
	public RpcProxy(String ip,int port){
		this(new InetSocketAddress(ip,port));
	}
	public RpcProxy(InetSocketAddress address){
		this(address,null);
	}
	public RpcProxy(String ip,int port,RpcClientConfig config){
		this(new InetSocketAddress(ip,port),config);
	}
	public RpcProxy(InetSocketAddress address,RpcClientConfig config){
		this(address,config,null);
	}
	public RpcProxy(InetSocketAddress address,RpcClientConfig config,Executor exec){
		this.address = address;
		this.config = config;
		this.exec = exec;
		this.rpcClient = RpcClient.getRpcClient(this.address,this.config,this.exec);
	} 
	
	public RpcProxy(RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		this(null,zookeeperConfig);
	}
	public RpcProxy(Executor exec,RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		this(null,exec,zookeeperConfig);
	}
	public RpcProxy(RpcClientConfig config,Executor exec,RpcZookeeperConfig zookeeperConfig) throws ZookeeperInitializationConfigErrorException{
		this.config = config;
		this.exec = exec;
		this.zookeeperConfig = zookeeperConfig;
		this.rpcClient = RpcClient.getRpcClient(this.config,this.exec,this.zookeeperConfig);
	} 
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                this
        );
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setMessageHead(new MessageHead(Constant._0XBC,Constant._0XAB,null,Counter.addOne()));
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
		request.setParameters(args);
		
		RpcResponse rpcResonse = this.rpcClient.getAvailableTcpClient(request).invoke(request);
		if(rpcResonse != null && rpcResonse.getException() != null){
			throw rpcResonse.getException();
		}
        return rpcResonse.getResultData();
	}
}
