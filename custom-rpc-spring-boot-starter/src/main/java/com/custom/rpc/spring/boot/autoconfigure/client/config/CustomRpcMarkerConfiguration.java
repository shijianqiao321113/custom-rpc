package com.custom.rpc.spring.boot.autoconfigure.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRpcMarkerConfiguration {

	@Bean
	public MarkerClient customRpcServerMarkerBean() {
		return new MarkerClient();
	}

	public class MarkerClient {
	}

}
