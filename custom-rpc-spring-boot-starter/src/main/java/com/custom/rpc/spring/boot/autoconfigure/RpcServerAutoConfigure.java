package com.custom.rpc.spring.boot.autoconfigure;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.custom.rpc.spring.boot.autoconfigure.server.config.CustomRpcMarkerConfiguration;
import com.custom.rpc.spring.boot.autoconfigure.server.config.CustomRpcServerConfig;
import com.custom.rpc.spring.boot.autoconfigure.server.config.CustomRpcTcpConfig;
import com.custom.rpc.spring.boot.autoconfigure.zookeeper.CustomRpcZookeeperConfig;
import com.rpc.exception.ZookeeperInitializationConfigErrorException;
import com.rpc.server.RpcServer;

@Configuration
@ConditionalOnBean(CustomRpcMarkerConfiguration.MarkerServer.class)
@EnableConfigurationProperties({CustomRpcTcpConfig.class,CustomRpcZookeeperConfig.class,CustomRpcServerConfig.class})
public class RpcServerAutoConfigure {
	
	@Autowired(required = false)
    private Executor executor;
	
	@Autowired
	private CustomRpcTcpConfig customRpcTcpConfig;
	
	@Autowired
	private CustomRpcZookeeperConfig customRpcZookeeperConfig;
	
	@Autowired
	private CustomRpcServerConfig customRpcServerConfig;
	
	@Bean
    @ConditionalOnMissingBean
    public RpcServer rpcServer() throws NumberFormatException, ZookeeperInitializationConfigErrorException {
		Assert.notNull(this.customRpcServerConfig.getPort(),"unspecified 'spring.custom.rpc.server.port' configuration");
		if(this.customRpcServerConfig.getZookeeperEnabled() == Boolean.TRUE){
			return RpcServer.start(this.customRpcServerConfig.getPort(), this.customRpcTcpConfig,this.executor,this.customRpcZookeeperConfig);
		}
		return RpcServer.start(this.customRpcServerConfig.getPort(), this.customRpcTcpConfig);
    }
}
