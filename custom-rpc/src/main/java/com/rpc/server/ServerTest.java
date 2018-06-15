package com.rpc.server;

import io.netty.channel.ChannelOption;

import java.util.LinkedHashMap;

import com.rpc.config.RpcServerConfig;
import com.rpc.config.RpcZookeeperConfig;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.testApi.ApiImpl;

public class ServerTest {
	public static void main(String[] args) {
		
		/*zookeeper 集群测试*/
		try {
			RpcServer.start(9001, new RpcServerConfig() {
				@Override
				public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
					LinkedHashMap<String,Class<?>> map = new LinkedHashMap<String,Class<?>>();
					map.put("com.rpc.testApi.Api", ApiImpl.class);
					return map;
				}
			}.setOptions(ChannelOption.SO_BACKLOG, 1024)
			.setOptions(ChannelOption.SO_SNDBUF, 36*1024)
			.setOptions(ChannelOption.SO_RCVBUF, 36*1024)
			,
			null,new RpcZookeeperConfig("127.0.0.1","127.0.0.1:2181","custom_rpc_1"));
		} catch (ZookeeperInitializationConfigErrorException e) {
			e.printStackTrace();
		}
		/*
		try {
			RpcServer.start(9002, new RpcServerConfig() {
				@Override
				public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
					LinkedHashMap<String,Class<?>> map = new LinkedHashMap<String,Class<?>>();
					map.put("com.rpc.testApi.Api", ApiImpl.class);
					return map;
				}
			},null,new RpcZookeeperConfig("127.0.0.1","127.0.0.1:2181","custom_rpc_1"));
		} catch (ZookeeperInitializationConfigErrorException e) {
			e.printStackTrace();
		}
		
		try {
			RpcServer.start(9003, new RpcServerConfig() {
				@Override
				public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
					LinkedHashMap<String,Class<?>> map = new LinkedHashMap<String,Class<?>>();
					map.put("com.rpc.testApi.Api", ApiImpl.class);
					return map;
				}
			},null,new RpcZookeeperConfig("127.0.0.1","127.0.0.1:2181","custom_rpc_1"));
		} catch (ZookeeperInitializationConfigErrorException e) {
			e.printStackTrace();
		}
		
		try {
			RpcServer.start(9004, new RpcServerConfig() {
				@Override
				public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
					LinkedHashMap<String,Class<?>> map = new LinkedHashMap<String,Class<?>>();
					map.put("com.rpc.testApi.Api", ApiImpl.class);
					return map;
				}
			},null,new RpcZookeeperConfig("127.0.0.1","127.0.0.1:2181","custom_rpc_1"));
		} catch (ZookeeperInitializationConfigErrorException e) {
			e.printStackTrace();
		}*/
		
		/*点对点测试*/
		/*RpcServer.start(8080, new RpcServerConfig() {
			@Override
			public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
				LinkedHashMap<String,Class<?>> map = new LinkedHashMap<String,Class<?>>();
				map.put("com.rpc.testApi.Api", ApiImpl.class);
				return map;
			}
		});*/
		
	}
}

