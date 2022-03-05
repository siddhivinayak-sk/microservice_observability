package com.sk.observabilityms.config.beans;

import java.io.Closeable;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.sk.observabilityms.model.KafkaMessage;

public class KafkaMessageSerializer implements Closeable, AutoCloseable, Serializer<KafkaMessage> {

    private JsonSerializer<KafkaMessage> sez = new JsonSerializer<>();

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public byte[] serialize(String s, KafkaMessage kafkaMessage) {
    	return sez.serialize(s, kafkaMessage);
    }

    @Override
    public void close() {
    	sez.close();
    }
}
