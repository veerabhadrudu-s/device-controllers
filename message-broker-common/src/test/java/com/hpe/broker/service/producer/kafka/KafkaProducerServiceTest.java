package com.hpe.broker.service.producer.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
		assertNotNull("KafkaProducerService cannot be null", kafkaProducerService);
	}

	@Test
	public void testKafkaProducerServiceGetName() {
		initKafkaProducer(keySerializerClass, valueSerializerClass);
		assertEquals("Expected KafkaProducerService name and actual name are not same", "kafka",
				kafkaProducerService.getName());
	}

	private void initKafkaProducer(String keySerializerClass, String valueSerializerClass) {
		kafkaProducerService = new KafkaProducerService<>(kafkaBootStrapServers, keySerializerClass,
				valueSerializerClass, producerClientId);
		kafkaProducerService.startService();
	}

}
