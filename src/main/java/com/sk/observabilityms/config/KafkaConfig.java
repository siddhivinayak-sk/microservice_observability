package com.sk.observabilityms.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;


@Configuration
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
	private boolean enabled;
	private List<Topic> topics;
	
	@Bean
	public void kafkaAdmins() {
		topics.stream().forEach(topic -> {
			Map<String, Object> configs = new HashMap<>();
			configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, topic.getHost() + ":" + topic.getPort());
			KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);
			NewTopic newTopic = new NewTopic(topic.getName(), 1, (short) 1);
			kafkaAdmin.createOrModifyTopics(newTopic);
		});
	}
	
	@Bean
	public List<KafkaTemplate<String, String>> kafkaTemplates() {
		List<KafkaTemplate<String, String>> kafkaTemplates = new ArrayList<>();
		topics.stream().filter(topic -> null != topic.getProducers() && !topic.getProducers().isEmpty()).forEach(topic -> {
			Map<String, Object> configProps = new HashMap<>();
	        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, topic.getHost() + ":" + topic.getPort());
	        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        configProps.put(ProducerConfig.RETRIES_CONFIG, 0);
	        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
	        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1);
	        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
	        DefaultKafkaProducerFactory<String, String> efpf = new DefaultKafkaProducerFactory<>(configProps);
	        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(efpf);
	        kafkaTemplate.setDefaultTopic(topic.getName());
	        kafkaTemplates.add(kafkaTemplate);
		});
		return kafkaTemplates;
	}
	
	@Bean
	public void kafkaConsumers() {
		topics.stream().filter(topic -> null != topic.getConsumers() && !topic.getConsumers().isEmpty()).forEach(topic -> {
			topic.getConsumers().stream().forEach(consumer -> {
				   ContainerProperties containerProps = new ContainerProperties(topic.getName());
				   containerProps.setMessageListener(new AcknowledgingMessageListener<String, String>() {
					@Override
					public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
						System.out.println("Message Received: " + data.key() + ":" + data.value());
					}
				   });
	 		       Map<String, Object> props = new HashMap<>();
			       props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, topic.getHost() + ":" + topic.getPort());
			       props.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroup());
			       props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			       props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			       props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
			       props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
			       props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");			       
			       KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(new DefaultKafkaConsumerFactory<>(props), containerProps);
			       container.start();
			});
		});
	}
	
	public void stringListener(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition) {
		System.out.println(message);
	}

	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public List<Topic> getTopics() {
		return topics;
	}


	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}
}

class Topic {
	private String name;
	private String host;
	private String port;
	private List<Producer> producers;
	private List<Consumer> consumers;
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
	public List<Producer> getProducers() {
		return producers;
	}
	public void setProducers(List<Producer> producers) {
		this.producers = producers;
	}
	public List<Consumer> getConsumers() {
		return consumers;
	}
	public void setConsumers(List<Consumer> consumers) {
		this.consumers = consumers;
	}
}

class Producer {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

class Consumer {
	private String name;
	private String group;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}