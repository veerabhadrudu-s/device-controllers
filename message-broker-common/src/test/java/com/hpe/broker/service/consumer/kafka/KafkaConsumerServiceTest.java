package com.hpe.broker.service.consumer.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutorService;

import org.junit.Test;

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
		assertNotNull("KafkaConsumerService cannot be null", kafkaConsumerService);
	}

	@Test
	public void testKafkaConsumerServiceGetName() {
		initKafkaConsumer(keyDeSerializerClass, valueDeSerializerClass);
		assertEquals("Expected KafkaConsumerService name and actual name are not same", "kafka",
				kafkaConsumerService.getName());
	}

	private void initKafkaConsumer(String keyDeSerializerClass, String valueDeSerializerClass) {
		kafkaConsumerService = new KafkaConsumerService<>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService);
	}

}
