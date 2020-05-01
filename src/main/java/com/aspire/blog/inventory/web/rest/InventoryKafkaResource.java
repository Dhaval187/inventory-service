package com.aspire.blog.inventory.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.blog.inventory.service.InventoryKafkaProducer;

@RestController
@RequestMapping(value = "/api/inventory-kafka")
public class InventoryKafkaResource {

	private final Logger log = LoggerFactory.getLogger(InventoryKafkaResource.class);

	private InventoryKafkaProducer kafkaProducer;

	public InventoryKafkaResource(InventoryKafkaProducer kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	@PostMapping(value = "/publish")
	public void sendMessageToKafkaTopic(@RequestParam("message") String message, @RequestParam("topic") String topic) {
		log.debug("REST request to send to Kafka topic the message : {}", message);
		this.kafkaProducer.sendMessage(topic, message);
	}
}
