package com.hpe.broker.service.consumer.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.hpe.broker.service.consumer.handler.LoggerBrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;

/**
 * @author sveera
 *
 */
public class KafkaConsumerServiceTest {

	private static final String KAFKA_TEST_TOPIC = "kafkaTestTopic";
	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keyDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String valueDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String consumerGroupId = this.getClass().getSimpleName();
	private LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler;
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
		assertEquals("Expected KafkaConsumerService name and actual name are not same", "kafka", kafkaConsumerService.getName());
	}

	public void testConsumeDataWithExternalDeSerializer() throws InterruptedException {
		initializeTestCaseInstanceData();
		initKafkaConsumer(keyDeSerializerClass, valueDeSerializerClass);
		startConsumerServiceAndValidateResult();
	}

	public void testPublishDataWithDefaultDeSerializer() throws InterruptedException {
		initializeTestCaseInstanceData();
		initKafkaConsumer("", "");
		startConsumerServiceAndValidateResult();

	}

	public void testConsumeDataWithConsumerGroup() throws InterruptedException {
		initializeTestCaseInstanceData();
		initKafkaConsumer("", "");
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler1 = new LoggerBrokerConsumerDataHandler();
		LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler2 = new LoggerBrokerConsumerDataHandler();
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler1);
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler2);
		waitForConsumptionOfData();
		kafkaConsumerService.stopService();
		executorService.shutdown();
		assertTrue("loggerBrokerConsumerDataHandler1 Consumer data length should be more than zero ",
				loggerBrokerConsumerDataHandler1.getAllConsumedMessages().size() > 0);
		assertTrue("loggerBrokerConsumerDataHandler2 Consumer data length should be more than zero ",
				loggerBrokerConsumerDataHandler2.getAllConsumedMessages().size() > 0);
	}

	private void startConsumerServiceAndValidateResult() throws InterruptedException {
		kafkaConsumerService.consumeData(KAFKA_TEST_TOPIC, loggerBrokerConsumerDataHandler);
		waitForConsumptionOfData();
		kafkaConsumerService.stopService();
		executorService.shutdown();
		List<String> consumerData = loggerBrokerConsumerDataHandler.getAllConsumedMessages();
		assertTrue("Consumer data length should be more than zero ", consumerData.size() > 0);
	}

	private void initializeTestCaseInstanceData() {
		executorService = Executors.newFixedThreadPool(5);
		loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler();
	}

	private void initKafkaConsumer(String keyDeSerializerClass, String valueDeSerializerClass) {
		kafkaConsumerService = new KafkaConsumerService<>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService);
	}

	private void waitForConsumptionOfData() throws InterruptedException {
		Thread.sleep(10000);
	}

}
