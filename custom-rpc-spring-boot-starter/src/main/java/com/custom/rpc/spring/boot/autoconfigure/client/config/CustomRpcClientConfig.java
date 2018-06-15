package com.custom.rpc.spring.boot.autoconfigure.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.custom.rpc.spring.boot.autoconfigure.util.ConstantCongfig;

@ConditionalOnMissingBean
@ConfigurationProperties(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT)
public class CustomRpcClientConfig {
	
	private String serverIp;

	private Integer serverPort;
	
	private Boolean zookeeperEnabled = Boolean.FALSE;

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public Boolean getZookeeperEnabled() {
		return zookeeperEnabled;
	}

	public void setZookeeperEnabled(Boolean zookeeperEnabled) {
		this.zookeeperEnabled = zookeeperEnabled;
	}

}
