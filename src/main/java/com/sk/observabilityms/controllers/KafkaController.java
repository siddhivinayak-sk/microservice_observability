package com.sk.observabilityms.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
public class KafkaController {

	@Autowired
	private List<KafkaTemplate<String, String>> templates;
	
	@GetMapping("/send")
	public String send(@RequestParam("message") String message) {
		templates.stream().filter(template -> template.getDefaultTopic().equalsIgnoreCase("topic1")).forEach(template -> {

			CompletableFuture<SendResult<String, String>> completableFuture =  template.send("topic1", "data", message);
			try {
				var result = completableFuture.get();
				System.out.println("Message Send: " + message + "    Offset:" + result.getRecordMetadata().offset());
			} catch(Exception ex) {
				System.out.println("Message falied: " + message);
			}
		});
		return "message posted";
	}
}
