package com.custom.rpc.spring.boot.autoconfigure.zookeeper;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

import com.custom.rpc.spring.boot.autoconfigure.util.ConstantCongfig;
import com.rpc.config.RpcZookeeperConfig;
import com.rpc.tools.Constant;

@ConditionalOnMissingBean
@ConfigurationProperties
public class CustomRpcZookeeperConfig extends RpcZookeeperConfig implements InitializingBean {
	
	@Autowired
    private Environment env;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!Constant.isNullOrEmpty(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_NETWORKIP))){
			super.setNetworkIP(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_NETWORKIP));
		}
		
		if(!Constant.isNullOrEmpty(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_CONNECTSTRING))){
			super.setConnectString(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_CONNECTSTRING));
		}

		if(!Constant.isNullOrEmpty(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_SERVERNAME))){
			super.setServerName(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_SERVERNAME));
		}

		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_SESSIONTIMEOUT,Integer.class) != null){
			super.setSessionTimeout(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_SESSIONTIMEOUT)));
		}

		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_CONNECTIONTIMEOUT,Integer.class) != null){
			super.setConnectionTimeout(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_ZOOKEEPER_CONNECTIONTIMEOUT)));
		}
	}
}
