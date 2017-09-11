package com.hpe.broker.service.kafka.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hpe.broker.service.consumer.kafka.KafkaConsumerServiceTest;
import com.hpe.broker.service.producer.kafka.KafkaProducerServiceTest;

/**
 * @author sveera
 *
 */
public class KafkaProducerConsumerServiceTest {
	private KafkaProducerServiceTest kafkaProducerServiceTest;
	private KafkaConsumerServiceTest kafkaConsumerServiceTest;

	@Before
	public void setUp() throws Exception {
		kafkaProducerServiceTest = new KafkaProducerServiceTest();
		kafkaConsumerServiceTest = new KafkaConsumerServiceTest();
	}

	@Ignore
	@Test
	public void testKafkaProducerConsumerServicesWithDefaultSettings() throws InterruptedException {
		kafkaProducerServiceTest.testPublishDataWithDefaultSerializers();
		kafkaConsumerServiceTest.testPublishDataWithDefaultDeSerializer();
	}

	@Ignore
	@Test
	public void testKafkaProducerConsumerServicesWithCustomSettings() throws InterruptedException {
		kafkaProducerServiceTest.testPublishDataWithExternalSerializers();
		kafkaConsumerServiceTest.testConsumeDataWithExternalDeSerializer();
	}

	@Ignore
	@Test
	public void testKafkaProducerConsumerServicesUsingConsumerGroup() throws InterruptedException {
		kafkaProducerServiceTest.testPublishDataWithExternalSerializers();
		kafkaConsumerServiceTest.testConsumeDataWithConsumerGroup();
	}

}
