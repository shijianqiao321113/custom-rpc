package com.custom.rpc.spring.boot.autoconfigure.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRpcMarkerConfiguration {

	@Bean
	public MarkerServer customRpcServerMarkerBean() {
		return new MarkerServer();
	}

	public class MarkerServer {
	}

}
