package com.custom.rpc.spring.boot.autoconfigure;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.custom.rpc.spring.boot.autoconfigure.client.config.CustomRpcClientConfig;
import com.custom.rpc.spring.boot.autoconfigure.client.config.CustomRpcClientTcpConfig;
import com.custom.rpc.spring.boot.autoconfigure.client.config.CustomRpcMarkerConfiguration;
import com.custom.rpc.spring.boot.autoconfigure.zookeeper.CustomRpcZookeeperConfig;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.proxy.RpcProxy;

@Configuration
@ConditionalOnBean(CustomRpcMarkerConfiguration.MarkerClient.class)
@EnableConfigurationProperties({CustomRpcClientTcpConfig.class,CustomRpcZookeeperConfig.class,CustomRpcClientConfig.class})
public class RpcClientAutoConfigure {

	@Autowired(required = false)
    private Executor executor;
	
	@Autowired
	private CustomRpcClientTcpConfig customRpcClientTcpConfig;
	
	@Autowired
	private CustomRpcZookeeperConfig customRpcZookeeperConfig;
	
	@Autowired
	private CustomRpcClientConfig customRpcClientConfig;
	
	@Bean
    @ConditionalOnMissingBean
    public RpcProxy rpcProxy() throws ZookeeperInitializationConfigErrorException{
		if(this.customRpcClientConfig.getZookeeperEnabled() == Boolean.TRUE){
			return new RpcProxy(customRpcClientTcpConfig, executor, customRpcZookeeperConfig);
		}
		Assert.notNull(this.customRpcClientConfig.getServerIp(),"unspecified 'spring.custom.rpc.client.serverIp' configuration");
		Assert.notNull(this.customRpcClientConfig.getServerPort(),"unspecified 'spring.custom.rpc.client.serverPort' configuration");
		return new RpcProxy(new InetSocketAddress(this.customRpcClientConfig.getServerIp(),this.customRpcClientConfig.getServerPort()),customRpcClientTcpConfig,executor);
    }
	
}
