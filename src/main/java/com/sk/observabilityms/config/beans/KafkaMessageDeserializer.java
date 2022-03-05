package com.sk.observabilityms.config.beans;

import java.io.Closeable;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.sk.observabilityms.model.KafkaMessage;

public class KafkaMessageDeserializer implements Closeable, AutoCloseable, Deserializer<KafkaMessage> {

	private JsonDeserializer<KafkaMessage> dsez = new JsonDeserializer<>();

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public KafkaMessage deserialize(String topic, byte[] bytes) {
    	return dsez.deserialize(topic, bytes);
    }

    @Override
    public void close() {
    	dsez.close();
    }
}
