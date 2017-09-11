/**
 * 
 */
package com.hpe.broker.service.consumer.factory.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.activemq.ActiveMQConsumerService;
import com.hpe.broker.service.consumer.factory.BrokerConsumerServiceFactory;
import com.hpe.broker.service.consumer.factory.impl.BrokerConsumerServiceFactoryImpl;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;

/**
 * @author sveera
 *
 */
public class BrokerConsumerServiceFactoryImplTest {

	private final String brokerURL = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000";
	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keyDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String valueDeSerializerClass = "org.apache.kafka.common.serialization.StringDeserializer";
	private final String consumerGroupId = this.getClass().getSimpleName();
	private ExecutorService executorService;
	private BrokerConsumerServiceFactory<String> brokerConsumerServiceFactory;

	@Before
	public void setUp() throws Exception {
		executorService = Executors.newFixedThreadPool(5);
		List<BrokerConsumerService<String>> brokerConsumerServices = new ArrayList<>();
		brokerConsumerServices.add(new KafkaConsumerService<String, String>(kafkaBootStrapServers, keyDeSerializerClass,
				valueDeSerializerClass, consumerGroupId, executorService));
		brokerConsumerServices.add(new ActiveMQConsumerService(brokerURL));
		brokerConsumerServiceFactory = new BrokerConsumerServiceFactoryImpl<>(brokerConsumerServices);
	}

	@Test
	public void testGetBrokerConsumerService() {
		assertTrue("Expected and actual BrokerConsumerService are not same",
				brokerConsumerServiceFactory.getBrokerConsumerService("activemq") instanceof ActiveMQConsumerService);
		assertTrue("Expected and actual BrokerConsumerService are not same",
				brokerConsumerServiceFactory.getBrokerConsumerService("kafka") instanceof KafkaConsumerService);
	}

}
