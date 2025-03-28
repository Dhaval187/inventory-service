package com.aspire.blog.inventory.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryKafkaConsumer {

	private final Logger log = LoggerFactory.getLogger(InventoryKafkaConsumer.class);
	private static final String TOPIC = "topic_inventory";

	@KafkaListener(topics = "topic_inventory", groupId = "group_id")
	public void consume(String message) throws IOException {
		log.info("Consumed message in {} : {}", TOPIC, message);
	}
}
