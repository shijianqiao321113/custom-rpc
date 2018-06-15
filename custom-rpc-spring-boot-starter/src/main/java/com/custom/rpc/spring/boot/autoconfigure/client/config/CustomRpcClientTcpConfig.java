package com.custom.rpc.spring.boot.autoconfigure.client.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

import com.custom.rpc.spring.boot.autoconfigure.util.ConstantCongfig;
import com.rpc.config.RpcClientConfig;

@ConditionalOnMissingBean
@ConfigurationProperties
public class CustomRpcClientTcpConfig extends RpcClientConfig implements InitializingBean{
	
	@Autowired
    private Environment env;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_READERIDLETIMESECONDS,Integer.class) != null){
			super.setReaderIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_READERIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_WRITERIDLETIMESECONDS,Integer.class) != null){
			super.setWriterIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_WRITERIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_ALLIDLETIMESECONDS,Integer.class) != null){
			super.setAllIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_ALLIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_WORKGROUPTHREADCOUNT,Integer.class) != null){
			super.setWorkGroupThreadCount(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_WORKGROUPTHREADCOUNT)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_HEARTBEATTNTERVALTIME,Integer.class) != null){
			super.setHeartbeatTntervalTime(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_HEARTBEATTNTERVALTIME)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_VALIDATIONLINKVALIDINTERVALTIME,Integer.class) != null){
			super.setValidationLinkValidIntervalTime(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_VALIDATIONLINKVALIDINTERVALTIME)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_CONNECTIONTIMEOUT,Long.class) != null){
			super.setConnectionTimeOut(Long.parseLong(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_CONNECTIONTIMEOUT)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_MAXFRAMELENGTH,Integer.class) != null){
			super.setMaxFrameLength(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_CLIENT_TCP_MAXFRAMELENGTH)));
		}
	}

}
