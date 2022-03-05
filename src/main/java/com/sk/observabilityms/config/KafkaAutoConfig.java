package com.sk.observabilityms.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.sk.observabilityms.model.KafkaMessage;


@Configuration
@ConfigurationProperties(prefix = "kafkaauto")
@ConditionalOnProperty(prefix = "kafkaauto", name = "enabled", havingValue = "true")
@EnableKafka
public class KafkaAutoConfig {
	private boolean enabled;
	private List<Topica> topics;
	
	
	@Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, topics.get(0).getHost() + ":" + topics.get(0).getPort());
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic topic2() {
         return new NewTopic(topics.get(0).getName(), 1, (short) 1);
    }	

    @Bean
    public NewTopic topic3() {
         return new NewTopic("topic3", 1, (short) 1);
    }	
    
    @Bean
    public NewTopic topic4() {
         return new NewTopic("topic4", 1, (short) 1);
    }	
    
    
    @Bean
    public ProducerFactory<String, KafkaMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, topics.get(0).getHost() + ":" + topics.get(0).getPort());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, KafkaMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }	
	
	//@Bean 
	public RecordMessageConverter messageConverter() {
		return new StringJsonMessageConverter();  
	}
	
    @Bean
    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, topics.get(0).getHost() + ":" + topics.get(0).getPort());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.sk.observabilityms.model");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaMessage.class);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //factory.setMessageConverter(messageConverter());
        return factory;
    }	
	
    @KafkaListener(topics = "topic2", groupId = "group1")
	public void listenWithHeadersTopic2(@Payload KafkaMessage message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
		System.out.println("T2 Received Message: " + message + " Partition: " + partition);
	}
	
    @KafkaListener(topics = "topic3", groupId = "group2")
	public void listenWithHeadersTopic3(KafkaMessage message) {
		System.out.println("T3 Received Message: " + message);
	}
	
    @KafkaListener(topics = "topic4", groupId = "group2")
	public void listenWithHeadersTopic4(KafkaMessage message) {
		System.out.println("T4 Received Message: " + message);
	}

    //@Bean
    public KafkaListenerErrorHandler myTopic3ErrorHandler() {
        return (m, e) -> {
        	System.out.println("Topic 3 Got an error: " + e.getMessage());
            return "some info about the failure";
        };
    }    
    
    //@Bean
    public KafkaListenerErrorHandler myTopic4ErrorHandler() {
        return (m, e) -> {
        	System.out.println("Topic 4 Got an error: " + e.getMessage());
            return "some info about the failure";
        };
    }    

    public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public List<Topica> getTopics() {
		return topics;
	}


	public void setTopics(List<Topica> topics) {
		this.topics = topics;
	}
}

class Topica {
	private String name;
	private String host;
	private String port;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
