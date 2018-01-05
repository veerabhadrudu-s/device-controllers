package com.hpe.broker.service.consumer.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class KafkaConsumerServiceTest {

	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keyDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String valueDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String consumerGroupId = "kafka-consumer-group-id";
	private ExecutorService executorService;
	private KafkaConsumerService<String, String> kafkaConsumerService;

	@Test
	public void testKafkaConsumerService() {
		initKafkaConsumer(keyDeSerializerClass, valueDeSerializerClass);
		assertNotNull(kafkaConsumerService, "KafkaConsumerService cannot be null");
	}

	@Test
	@DisplayName("test Kafka Consumer Service Get Name")
	public void testKafkaConsumerServiceGetName() {
		initKafkaConsumer(keyDeSerializerClass, valueDeSerializerClass);
		assertEquals("kafka", kafkaConsumerService.getName(),
				"Expected KafkaConsumerService name and actual name are not same");
	}

	private void initKafkaConsumer(String keyDeSerializerClass, String valueDeSerializerClass) {
		kafkaConsumerService = new KafkaConsumerService<>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService);
	}

}
