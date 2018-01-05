package com.hpe.broker.service.kafka.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.broker.executor.mock.MockManagedExecutorService;
import com.hpe.broker.service.consumer.handler.LoggerBrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;
import com.hpe.broker.service.kafka.KafkaBrokerService;
import com.hpe.broker.service.producer.kafka.KafkaProducerService;

/**
 * @author sveera
 *
 */
public class KafkaProducerConsumerServiceTest {

	private static final int DATA_LENGTH = 1000;
	private static final ExecutorService executorService = new MockManagedExecutorService();
	private static final String KAFKA_TEST_TOPIC = "kafkaTestTopic";
	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String keyDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String valueDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String consumerGroupId = "kafka-consumer-group-id";
	private final String producerClientId = "kafka-producer-id";

	private final LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler1 = new LoggerBrokerConsumerDataHandler();
	private final LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler2 = new LoggerBrokerConsumerDataHandler();
	private LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler;
	private KafkaConsumerService<String, String> kafkaConsumerService;
	private KafkaProducerService<String, String> kafkaProducerService;
	private static KafkaBrokerService kafkaBrokerService;

	@BeforeAll
	public static void setUp() throws Exception {
		kafkaBrokerService = new KafkaBrokerService(9092, "target/kafka-data/tmp");
		kafkaBrokerService.startService();
	}

	@AfterAll
	public static void tearDown() {
		kafkaBrokerService.stopService();
		executorService.shutdown();
	}

	@Test
	@DisplayName("test KafkaProducer Consumer Services With Default Settings")
	public void testKafkaProducerConsumerServicesWithDefaultSettings() throws InterruptedException {
		loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler();
		testConsumeDataWithDefaultDeSerializer();
		testPublishDataWithDefaultSerializers();
		waitForDataProcessing();
		List<String> consumerData = loggerBrokerConsumerDataHandler.getAllConsumedMessages();
		assertEquals(DATA_LENGTH, consumerData.size(), "Expected and actual consumed data length are not same");
		kafkaConsumerService.stopService();
	}

	@Test
	@DisplayName("test KafkaProducer Consumer Services With Custom Settings")
	public void testKafkaProducerConsumerServicesWithCustomSettings() throws InterruptedException {
		loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler();
		testConsumeDataWithExternalDeSerializer();
		testPublishDataWithExternalSerializers();
		waitForDataProcessing();
		List<String> consumerData = loggerBrokerConsumerDataHandler.getAllConsumedMessages();
		assertEquals(DATA_LENGTH, consumerData.size(), "Expected and actual consumed data length are not same");
		kafkaConsumerService.stopService();
	}

	@Test
	@DisplayName("test KafkaProducer Consumer Services UsingConsumerGroup")
	public void testKafkaProducerConsumerServicesUsingConsumerGroup() throws InterruptedException {
		testConsumeDataWithConsumerGroup();
		testPublishDataWithExternalSerializers();
		waitForDataProcessing();
		assertEquals(DATA_LENGTH,
				loggerBrokerConsumerDataHandler1.getAllConsumedMessages().size()
						+ loggerBrokerConsumerDataHandler2.getAllConsumedMessages().size(),
				"Expected and actual consumed data length are not same");

		kafkaConsumerService.stopService();
	}

	public void testPublishDataWithExternalSerializers() throws InterruptedException {
		initKafkaProducer(keySerializerClass, valueSerializerClass);
		publishData();
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();

	}

	public void testPublishDataWithDefaultSerializers() throws InterruptedException {
		initKafkaProducer("", "");
		publishData();
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();

	}

	private void publishData() {
		for (int messageIndex = 0; messageIndex < DATA_LENGTH; messageIndex++)
			kafkaProducerService.publishData(KAFKA_TEST_TOPIC,
					"This device data generated  @ " + new Date().toString());
	}

	private void initKafkaProducer(String keySerializerClass, String valueSerializerClass) {
		kafkaProducerService = new KafkaProducerService<>(kafkaBootStrapServers, keySerializerClass,
				valueSerializerClass, producerClientId);
		kafkaProducerService.startService();
	}

	private void waitForBrokerAcknowledgement() throws InterruptedException {
		Thread.sleep(2000);
	}

	public void testConsumeDataWithExternalDeSerializer() throws InterruptedException {
		initKafkaConsumer(keyDeSerializerClass, valueDeSerializerClass);
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler);
		waitForConsumerToInitialize();
	}

	public void testConsumeDataWithDefaultDeSerializer() throws InterruptedException {
		initKafkaConsumer("", "");
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler);
		waitForConsumerToInitialize();
	}

	public void testConsumeDataWithConsumerGroup() throws InterruptedException {
		initKafkaConsumer("", "");
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler1);
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler2);
	}

	private void initKafkaConsumer(String keyDeSerializerClass, String valueDeSerializerClass) {
		kafkaConsumerService = new KafkaConsumerService<>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService);
	}

	private void waitForConsumerToInitialize() throws InterruptedException {
		Thread.sleep(5000);
	}

	private void waitForDataProcessing() throws InterruptedException {
		Thread.sleep(15000);
	}

}
