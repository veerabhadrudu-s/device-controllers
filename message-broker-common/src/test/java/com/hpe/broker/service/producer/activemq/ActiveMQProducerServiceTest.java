package com.hpe.broker.service.producer.activemq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hpe.broker.service.activemq.EmbeddedActivemqBroker;

/**
 * @author sveera
 *
 */
public class ActiveMQProducerServiceTest {

	private final String brokerURL = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000";
	private final String embeddedBrokerUrl = "tcp://localhost:61616?maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600&amp;wireFormat.maxInactivityDuration=120000";
	private ActiveMQProducerService activeMQProducerService;
	private EmbeddedActivemqBroker embeddedActivemqBroker;

	@BeforeEach
	public void setUp() throws Exception {
		embeddedActivemqBroker = new EmbeddedActivemqBroker(embeddedBrokerUrl);
		embeddedActivemqBroker.startService();
		activeMQProducerService = new ActiveMQProducerService(brokerURL);

	}

	@AfterEach
	public void tearDown() {
		activeMQProducerService.stopService();
		embeddedActivemqBroker.stopService();
	}

	@Test
	public void testActiveMQProducerService() {
		assertNotNull(activeMQProducerService,"activeMQProducerService Cannot be null");
	}

	@Test
	public void testActiveMQProducerServiceGetName() {
		assertEquals( "activemq",
				activeMQProducerService.getName(),"Expected ActiveMQProducerService name and actual name are not same");
	}

	@Test
	public void testPublishData() throws InterruptedException {
		for (int deviceMessageIndex = 0; deviceMessageIndex < 100; deviceMessageIndex++)
			activeMQProducerService.publishData("activeMQTestQueue",
					"This device data generated  @ " + new Date().toString());
		waitForProducerToFinish();
	}

	private void waitForProducerToFinish() throws InterruptedException {
		Thread.sleep(3000);
	}

}
