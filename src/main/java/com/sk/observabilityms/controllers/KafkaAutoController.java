package com.sk.observabilityms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.observabilityms.model.KafkaMessage;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/kafkaauto")
@ConditionalOnProperty(prefix = "kafkaauto", name = "enabled", havingValue = "true")
public class KafkaAutoController {

	@Autowired
	private KafkaTemplate<String, KafkaMessage> kafkaTemplate;
	
	@GetMapping("/send")
	public String send(@RequestParam("message") String message) {
		KafkaMessage kMessage = new KafkaMessage();
		kMessage.setMessage(message);
		kMessage.setMessageCode("200");

		CompletableFuture<SendResult<String, KafkaMessage>> completableFuture =  kafkaTemplate.send("topic2", "data", kMessage);
		try {
			var result = completableFuture.get();
			System.out.println("Message Send: " + message + "    Offset:" + result.getRecordMetadata().offset());
		} catch(Exception ex) {
			System.out.println("Message failed: " + message);
		}
		return "message posted";
	}
	
	
}
