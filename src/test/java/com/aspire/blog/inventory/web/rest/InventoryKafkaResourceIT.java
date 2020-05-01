package com.aspire.blog.inventory.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aspire.blog.inventory.InventoryApp;
import com.aspire.blog.inventory.service.InventoryKafkaProducer;

@EmbeddedKafka
@SpringBootTest(classes = InventoryApp.class)
public class InventoryKafkaResourceIT {

	@Autowired
	private InventoryKafkaProducer kafkaProducer;

	private MockMvc restMockMvc;

	@BeforeEach
	public void setup() {
		InventoryKafkaResource kafkaResource = new InventoryKafkaResource(kafkaProducer);

		this.restMockMvc = MockMvcBuilders.standaloneSetup(kafkaResource).build();
	}

	@Test
	public void sendMessageToKafkaTopic() throws Exception {
		restMockMvc.perform(post("/api/inventory-kafka/publish?topic=test&message=yolo")).andExpect(status().isOk());
	}
}
