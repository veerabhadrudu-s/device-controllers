/**
 * 
 */
package com.hpe.broker.service.consumer.activemq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.hpe.broker.service.activemq.EmbeddedActivemqBroker;
import com.hpe.broker.service.consumer.handler.LoggerBrokerConsumerDataHandler;
import com.hpe.broker.service.producer.activemq.ActiveMQProducerService;

/**
 * @author sveera
 *
 */
public class ActiveMQConsumerServiceTest {
	private final Logger logger = getLogger(getClass());

	private static final int PRODUCER_MSG_COUNT = 100;
	private final String destination = "activeMQTestQueue";
	private ActiveMQConsumerService activeMQConsumerService;
	private final String brokerURL = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000";
	private final String embeddedBrokerUrl = "tcp://localhost:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;wireFormat.maxInactivityDuration=120000";
	private ActiveMQProducerService activeMQProducerService;
	private EmbeddedActivemqBroker embeddedActivemqBroker;
	private LoggerBrokerConsumerDataHandler loggerBrokerConsumerDataHandler;

	@BeforeEach
	public void setUp() throws Exception {
		loggerBrokerConsumerDataHandler = new LoggerBrokerConsumerDataHandler();
		embeddedActivemqBroker = new EmbeddedActivemqBroker(embeddedBrokerUrl);
		embeddedActivemqBroker.startService();
		activeMQProducerService = new ActiveMQProducerService(brokerURL);
		activeMQConsumerService = new ActiveMQConsumerService(brokerURL);

	}

	@AfterEach
	public void tearDown() {
		activeMQConsumerService.stopService();
		activeMQProducerService.stopService();
		embeddedActivemqBroker.stopService();
	}

	@Test
	public void testActiveMQConsumerService() {
	}

	@Test
	@DisplayName("test ActiveMQConsumer Service Get Name")
	public void testActiveMQConsumerServiceGetName() {
		assertEquals("activemq", activeMQConsumerService.getName(),
				"Expected ActiveMQConsumerService name and actual name are not same");
	}

	@Test
	@DisplayName("test ActiveMQConsumer Service Consume Data")
	public void testActiveMQConsumerServiceConsumeData() throws InterruptedException {
		trySendingMessagesToQueue();
		activeMQConsumerService.consumeData(destination, loggerBrokerConsumerDataHandler);
		waitForConsumerToFinish();
		logger.debug("Total Messages consumed by " + ActiveMQConsumerService.class.getSimpleName() + " is "
				+ loggerBrokerConsumerDataHandler.getAllConsumedMessages().size());
		assertTrue(loggerBrokerConsumerDataHandler.getAllConsumedMessages().size() == PRODUCER_MSG_COUNT,
				"Expected message count and actual message count are not same");
	}

	private void trySendingMessagesToQueue() {
		for (int deviceMessageIndex = 0; deviceMessageIndex < PRODUCER_MSG_COUNT; deviceMessageIndex++)
			activeMQProducerService.publishData(destination, "This device data generated  @ " + new Date().toString());
	}

	private void waitForConsumerToFinish() throws InterruptedException {
		Thread.sleep(5000);
	}
}
