package com.sk.observabilityms.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfiguration {
	
	@Value("${applicaiton.domain.url:localhost}")
	private String domain;

	@Bean
	public OpenAPI springShopOpenAPI() {
	    return new OpenAPI()
	            .info(new Info().title("SpringBoot Observability")
	            .description("SpringBoot Observability sample application")
	            .version("v0.0.1")
	            .license(new License().name("Apache 2.0").url("https://github.com/siddhivinayak-sk")))
	            .externalDocs(new ExternalDocumentation()
	            .description("SpringBoot Observability Wiki Documentation")
	            .url("https://github.com/siddhivinayak-sk"));
	}	
	
	
	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi
			.builder()
			.group("system")
			.pathsToMatch("/**")
			.build();
	}
	
}
