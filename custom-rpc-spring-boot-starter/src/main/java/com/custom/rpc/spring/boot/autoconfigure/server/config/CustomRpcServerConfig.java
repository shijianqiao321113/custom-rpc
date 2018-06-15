package com.custom.rpc.spring.boot.autoconfigure.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.custom.rpc.spring.boot.autoconfigure.util.ConstantCongfig;

@ConditionalOnMissingBean
@ConfigurationProperties(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER)
public class CustomRpcServerConfig {

	private Integer port;
	
	private Boolean zookeeperEnabled = Boolean.FALSE;

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Boolean getZookeeperEnabled() {
		return zookeeperEnabled;
	}

	public void setZookeeperEnabled(Boolean zookeeperEnabled) {
		this.zookeeperEnabled = zookeeperEnabled;
	}
	
	

}
