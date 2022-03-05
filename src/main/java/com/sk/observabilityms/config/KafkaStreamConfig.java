package com.sk.observabilityms.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.sk.observabilityms.config.beans.KafkaMessageSerde;
import com.sk.observabilityms.model.KafkaMessage;

/**
 * This KafkaStream receives KafkaMessage from topic2 and
 * checks if message contains colon (:) symbol, it message
 * contains colon then message splits into two portion and
 * send to topic3 and topic4. If message does not contains
 * colon then message only forwarded to topic3.
 * 
 *                          topic2
 *                            | |
 *                            | |
 *                           /   \
 *                          /  ^  \
 *                         /  / \  \
 *                        /  /   \  \
 *                       /  /     \  \
 *                      /  /       \  \
 *                    topic3       topic4
 * 
 * @author Sandeep Kumar
 *
 */
@Configuration
@ConfigurationProperties(prefix = "kafkaauto")
@ConditionalOnProperty(prefix = "kafkaauto", name = {"enabled", "streamed"}, havingValue = "true")
@EnableKafkaStreams
public class KafkaStreamConfig {
	private boolean enabled;
	private boolean streamed;
	private List<Topicb> topics;
	
	@Bean 
	public RecordMessageConverter messageConverter() {
		return new StringJsonMessageConverter();  
	}
	
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, topics.get(0).getHost() + ":" + topics.get(0).getPort());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaMessageSerde.class.getName());

        return new KafkaStreamsConfiguration(props);
    }
	
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<KafkaMessage> KM_SERDE = new KafkaMessageSerde();
    private static final Serde<KafkaMessage> KM_SERDE1 = new JsonSerde<>(KafkaMessage.class);
    
    @Bean
    public KStream<String, KafkaMessage> buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, KafkaMessage> messageStream = streamsBuilder.stream("topic2", Consumed.with(STRING_SERDE, KM_SERDE1));

        messageStream
        .mapValues(value -> {
        	KafkaMessage km = new KafkaMessage();
        	km.setMessageCode(value.getMessageCode());
        	km.setMessage(value.getMessage().split(":")[0]);
        	return km;
        })
        .to("topic3", Produced.with(STRING_SERDE, KM_SERDE1));
        
        messageStream
        .filter((key, value) -> value.getMessage().contains(":"))
        .mapValues(value -> {
        	KafkaMessage km = new KafkaMessage();
        	km.setMessageCode(value.getMessageCode());
        	km.setMessage(value.getMessage().split(":")[1]);
        	return km;
        })
        .to("topic4", Produced.with(STRING_SERDE, KM_SERDE1));
        
        //messageStream.print(Printed.toSysOut());
        return messageStream;
    }

    
    
    
    
    
    
    
    
    
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isStreamed() {
		return streamed;
	}

	public void setStreamed(boolean streamed) {
		this.streamed = streamed;
	}

	public List<Topicb> getTopics() {
		return topics;
	}

	public void setTopics(List<Topicb> topics) {
		this.topics = topics;
	}
}

class Topicb {
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
