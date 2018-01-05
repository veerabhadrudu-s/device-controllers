package com.hpe.broker.service.producer.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class KafkaProducerServiceTest {

	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String producerClientId = this.getClass().getSimpleName();
	private KafkaProducerService<String, String> kafkaProducerService;

	@Test
	public void testKafkaProducerService() throws Exception, IllegalAccessException, ClassNotFoundException {
		initKafkaProducer(keySerializerClass, valueSerializerClass);
		assertNotNull(kafkaProducerService, "KafkaProducerService cannot be null");
	}

	@Test
	public void testKafkaProducerServiceGetName() {
		initKafkaProducer(keySerializerClass, valueSerializerClass);
		assertEquals("kafka", kafkaProducerService.getName(),
				"Expected KafkaProducerService name and actual name are not same");
	}

	private void initKafkaProducer(String keySerializerClass, String valueSerializerClass) {
		kafkaProducerService = new KafkaProducerService<>(kafkaBootStrapServers, keySerializerClass,
				valueSerializerClass, producerClientId);
		kafkaProducerService.startService();
	}

}
