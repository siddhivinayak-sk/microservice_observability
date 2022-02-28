package com.sk.observabilityms.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.catalina.connector.Connector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedTomcatConfiguration {

	@Value("${server.additionalPorts:}")
	private String ports;
	
	@Value("${server.protocol:http}")
	private String protocol;

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
	    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	    Connector[] additionalConnectors = this.additionalConnector();
	    if (additionalConnectors != null && additionalConnectors.length > 0) {
	        tomcat.addAdditionalTomcatConnectors(additionalConnectors);
	    }
	    return tomcat;
	} 
	
    private Connector[] additionalConnector() {
        if (StringUtils.isBlank(this.ports)) {
            return null;
        }
        List<Connector> connectors =
        Stream.of(ports.split(",")).map(port -> {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme(protocol);
            connector.setPort(Integer.valueOf(port));
            return connector;
        }).collect(Collectors.toList());
        return connectors.toArray(new Connector[] {});
    }    
	
}
