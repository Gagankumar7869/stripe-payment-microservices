package com.hulkhiretech.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {
	@Bean
	 RestClient restClient(Builder builder) {
		log.info("Creating RestClient bean ....");
		
		
		return builder.build() ;
		
	}

}
