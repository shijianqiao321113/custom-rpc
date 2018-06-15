package com.custom.rpc.spring.boot.autoconfigure.server.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import com.custom.rpc.spring.boot.autoconfigure.server.annotation.ApiService;
import com.custom.rpc.spring.boot.autoconfigure.util.ConstantCongfig;
import com.rpc.config.RpcServerConfig;

@ConditionalOnMissingBean
@ConfigurationProperties
public class CustomRpcTcpConfig extends RpcServerConfig implements InitializingBean,ApplicationContextAware{
	
	private static final Log logger = LogFactory.getLog(CustomRpcTcpConfig.class);
	
	@Autowired
    private Environment env;
	
	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_READERIDLETIMESECONDS,Integer.class) != null){
			super.setReaderIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_READERIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_WRITERIDLETIMESECONDS,Integer.class) != null){
			super.setWriterIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_WRITERIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_ALLIDLETIMESECONDS,Integer.class) != null){
			super.setAllIdleTimeSeconds(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_ALLIDLETIMESECONDS)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_BOSSGROUPTHREADCOUNT,Integer.class) != null){
			super.setBossGroupThreadCount(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_BOSSGROUPTHREADCOUNT)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_WORKGROUPTHREADCOUNT,Integer.class) != null){
			super.setWorkGroupThreadCount(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_WORKGROUPTHREADCOUNT)));
		}
		
		if(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_MAXFRAMELENGTH,Integer.class) != null){
			super.setMaxFrameLength(Integer.parseInt(env.getProperty(ConstantCongfig.SPRING_CUSTOM_RPC_SERVER_TCP_MAXFRAMELENGTH)));
		}
	}

	@Override
	public LinkedHashMap<String, Class<?>> initServiceMappingConfig() {
		LinkedHashMap<String, Class<?>> apiMap = new LinkedHashMap<String, Class<?>>();
		Map<String, Object> beanMap = this.applicationContext.getBeansWithAnnotation(ApiService.class);
		for(Map.Entry<String, Object> entry:beanMap.entrySet()){
			Class<? extends Object> clazz = entry.getValue().getClass();
			Class<? extends Object>  [] interfaces = clazz.getInterfaces();  
		    for(Class<? extends Object>  inter : interfaces){  
		    	if(apiMap.containsKey(inter.getName())){
		    		logger.warn("api interface mapping '"+inter.getName()+"' implement class coverage");
		    	}
		    	apiMap.put(inter.getName(), clazz);
		    }
		}
		for(Map.Entry<String, Class<?>> m:apiMap.entrySet()){
			logger.info("api interface mapping '"+m.getKey()+"' --> '"+m.getValue().getName()+"'");
		}
		return apiMap;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
