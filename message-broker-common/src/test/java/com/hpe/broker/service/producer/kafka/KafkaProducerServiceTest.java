package com.hpe.broker.service.producer.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.hpe.broker.service.producer.kafka.KafkaProducerService;

/**
 * @author sveera
 *
 */
public class KafkaProducerServiceTest {

	private static final String KAFKA_TEST_TOPIC = "kafkaTestTopic";
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

	public void testPublishDataWithExternalSerializers() throws InterruptedException {
		initKafkaProducer(keySerializerClass, valueSerializerClass);
		for (int messageIndex = 0; messageIndex < 20000; messageIndex++)
			kafkaProducerService.publishData(KAFKA_TEST_TOPIC,
					"This device data generated  @ " + new Date().toString());
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();

	}

	public void testPublishDataWithDefaultSerializers() throws InterruptedException {
		initKafkaProducer("", "");
		kafkaProducerService.publishData(KAFKA_TEST_TOPIC, "This device data generated  @ " + new Date().toString());
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();

	}

	private void initKafkaProducer(String keySerializerClass, String valueSerializerClass) {
		kafkaProducerService = new KafkaProducerService<>(kafkaBootStrapServers, keySerializerClass,
				valueSerializerClass, producerClientId);
		kafkaProducerService.startService();
	}

	private void waitForBrokerAcknowledgement() throws InterruptedException {
		Thread.sleep(1000);
	}

}
