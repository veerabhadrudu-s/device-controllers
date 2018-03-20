package com.hpe.broker.service.kafka.test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.broker.executor.mock.MockManagedExecutorService;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.handler.LoggerBrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;
import com.hpe.broker.service.kafka.KafkaBrokerService;
import com.hpe.broker.service.producer.kafka.KafkaProducerService;

/**
 * @author sveera
 *
 */
public class KafkaProducerConsumerServiceTest {

	private static final int CONSUMER_INITIALIZATION_WAIT_TIME = 10000;
	private static final int DATA_LENGTH = 200;
	private static final long WAIT_PERIOD_FOR_PROCESSING = 20000l;
	private static final ExecutorService executorService = new MockManagedExecutorService();
	private static final String KAFKA_TEST_TOPIC = "kafkaTestTopic";
	private static KafkaBrokerService kafkaBrokerService;
	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String keyDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String valueDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String consumerGroupId = "kafka-consumer-group-id";
	private final String producerClientId = "kafka-producer-id";

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
		CountDownLatch countDownLatch = new CountDownLatch(DATA_LENGTH);
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler(
				countDownLatch);
		KafkaConsumerService<String, String> kafkaConsumerService = testConsumeDataWithDefaultDeSerializer(
				loggerBrokerConsumerDataHandler);
		testPublishDataWithDefaultSerializers();
		countDownLatch.await(WAIT_PERIOD_FOR_PROCESSING, MILLISECONDS);
		List<String> consumerData = loggerBrokerConsumerDataHandler.getAllConsumedMessages();
		assertEquals(DATA_LENGTH, consumerData.size(), "Expected and actual consumed data length are not same");
		kafkaConsumerService.stopService();
	}

	@Test
	@DisplayName("test KafkaProducer Consumer Services With Custom Settings")
	public void testKafkaProducerConsumerServicesWithCustomSettings() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(DATA_LENGTH);
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler(
				countDownLatch);
		KafkaConsumerService<String, String> kafkaConsumerService = testConsumeDataWithExternalDeSerializer(
				loggerBrokerConsumerDataHandler);
		testPublishDataWithExternalSerializers();
		countDownLatch.await(WAIT_PERIOD_FOR_PROCESSING, MILLISECONDS);
		List<String> consumerData = loggerBrokerConsumerDataHandler.getAllConsumedMessages();
		assertEquals(DATA_LENGTH, consumerData.size(), "Expected and actual consumed data length are not same");
		kafkaConsumerService.stopService();
	}

	@Test
	@DisplayName("test KafkaProducer Consumer Services UsingConsumerGroup")
	public void testKafkaProducerConsumerServicesUsingConsumerGroup() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(DATA_LENGTH);
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler1 = new LoggerBrokerConsumerDataHandler(
				countDownLatch);
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler2 = new LoggerBrokerConsumerDataHandler(
				countDownLatch);
		KafkaConsumerService<String, String> kafkaConsumerService = testConsumeDataWithConsumerGroup(
				loggerBrokerConsumerDataHandler1, loggerBrokerConsumerDataHandler2);
		testPublishDataWithExternalSerializers();
		countDownLatch.await(WAIT_PERIOD_FOR_PROCESSING, MILLISECONDS);
		assertEquals(DATA_LENGTH,
				loggerBrokerConsumerDataHandler1.getAllConsumedMessages().size()
						+ loggerBrokerConsumerDataHandler2.getAllConsumedMessages().size(),
				"Expected and actual consumed data length are not same");
		kafkaConsumerService.stopService();
	}

	public void testPublishDataWithExternalSerializers() throws InterruptedException {
		KafkaProducerService<String, String> kafkaProducerService = initKafkaProducer(keySerializerClass,
				valueSerializerClass);
		publishData(kafkaProducerService);
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();

	}

	public void testPublishDataWithDefaultSerializers() throws InterruptedException {
		KafkaProducerService<String, String> kafkaProducerService = initKafkaProducer("", "");
		publishData(kafkaProducerService);
		waitForBrokerAcknowledgement();
		kafkaProducerService.stopService();
	}

	private void publishData(KafkaProducerService<String, String> kafkaProducerService) {
		for (int messageIndex = 0; messageIndex < DATA_LENGTH; messageIndex++)
			kafkaProducerService.publishData(KAFKA_TEST_TOPIC,
					"This device data generated  @ " + new Date().toString());
	}

	private KafkaProducerService<String, String> initKafkaProducer(String keySerializerClass,
			String valueSerializerClass) {
		KafkaProducerService<String, String> kafkaProducerService = new KafkaProducerService<>(kafkaBootStrapServers,
				keySerializerClass, valueSerializerClass, producerClientId);
		kafkaProducerService.startService();
		return kafkaProducerService;
	}

	private void waitForBrokerAcknowledgement() throws InterruptedException {
		Thread.sleep(2000);
	}

	public KafkaConsumerService<String, String> testConsumeDataWithExternalDeSerializer(
			LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler) throws InterruptedException {
		KafkaConsumerServiceTestSpecific<String, String> kafkaConsumerService = initKafkaConsumer(keyDeSerializerClass,
				valueDeSerializerClass);
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler);
		waitForConsumerToInitialize(kafkaConsumerService.countDownLatchs);
		return kafkaConsumerService;
	}

	public KafkaConsumerService<String, String> testConsumeDataWithDefaultDeSerializer(
			LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler) throws InterruptedException {
		KafkaConsumerServiceTestSpecific<String, String> kafkaConsumerService = initKafkaConsumer("", "");
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler);
		waitForConsumerToInitialize(kafkaConsumerService.countDownLatchs);
		return kafkaConsumerService;
	}

	public KafkaConsumerService<String, String> testConsumeDataWithConsumerGroup(
			LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler1,
			LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler2) throws InterruptedException {
		KafkaConsumerServiceTestSpecific<String, String> kafkaConsumerService = initKafkaConsumer("", "");
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler1);
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler2);
		waitForConsumerToInitialize(kafkaConsumerService.countDownLatchs);
		return kafkaConsumerService;
	}

	private KafkaConsumerServiceTestSpecific<String, String> initKafkaConsumer(String keyDeSerializerClass,
			String valueDeSerializerClass) {
		return new KafkaConsumerServiceTestSpecific<>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService);
	}

	private void waitForConsumerToInitialize(List<CountDownLatch> countDownLatchs) throws InterruptedException {
		for (CountDownLatch countDownLatch : countDownLatchs)
			countDownLatch.await(CONSUMER_INITIALIZATION_WAIT_TIME, MILLISECONDS);
	}

	private class KafkaConsumerServiceTestSpecific<K, V> extends KafkaConsumerService<K, V> {

		private List<CountDownLatch> countDownLatchs = new ArrayList<>();

		public KafkaConsumerServiceTestSpecific(String kafkaBootStrapServers, String keyDeSerializerClass,
				String valueDeSerializerClass, String consumerGroupId, ExecutorService executorService) {
			super(kafkaBootStrapServers, keyDeSerializerClass, valueDeSerializerClass, consumerGroupId,
					executorService);
		}

		@Override
		public void consumeData(String destination, BrokerConsumerDataHandler<V> brokerConsumerDataHandler) {
			CountDownLatch countDownLatch = new CountDownLatch(1);
			countDownLatchs.add(countDownLatch);
			KafkaConsumerRunnableTestSpecific kafkaConsumerRunnable = new KafkaConsumerRunnableTestSpecific(destination,
					brokerConsumerDataHandler, countDownLatch);
			startConsumerThread(kafkaConsumerRunnable);
		}

		private class KafkaConsumerRunnableTestSpecific extends KafkaConsumerRunnable {
			private final CountDownLatch countDownLatch;

			public KafkaConsumerRunnableTestSpecific(String destination,
					BrokerConsumerDataHandler<V> brokerConsumerDataHandler, CountDownLatch countDownLatch) {
				super(destination, brokerConsumerDataHandler);
				this.countDownLatch = countDownLatch;
			}

			@Override
			protected void startPollingForRecords() {
				super.startPollingForRecords();
				countDownLatch.countDown();
			}
		}

	}

}
