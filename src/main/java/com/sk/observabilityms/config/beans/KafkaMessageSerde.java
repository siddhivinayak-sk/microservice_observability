package com.sk.observabilityms.config.beans;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.sk.observabilityms.model.KafkaMessage;

public class KafkaMessageSerde implements Serde<KafkaMessage> {
    private KafkaMessageSerializer serializer = new KafkaMessageSerializer();
    private KafkaMessageDeserializer deserializer = new KafkaMessageDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<KafkaMessage> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<KafkaMessage> deserializer() {
        return deserializer;
    }
}
