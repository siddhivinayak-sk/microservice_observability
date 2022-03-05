package com.sk.observabilityms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sk.observabilityms.config.OpenAPIConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableTransactionManagement
@EnableWebMvc
@EnableScheduling
@Import({OpenAPIConfiguration.class})
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class ObservabilitymsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObservabilitymsApplication.class, args);
	}

}
