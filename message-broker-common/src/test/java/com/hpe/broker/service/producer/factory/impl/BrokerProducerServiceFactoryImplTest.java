/**
 * 
 */
package com.hpe.broker.service.producer.factory.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hpe.broker.service.producer.BrokerProducerService;
import com.hpe.broker.service.producer.activemq.ActiveMQProducerService;
import com.hpe.broker.service.producer.factory.BrokerProducerServiceFactory;
import com.hpe.broker.service.producer.kafka.KafkaProducerService;

/**
 * @author sveera
 *
 */
public class BrokerProducerServiceFactoryImplTest {

	private final String brokerURL = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000";
	private final String kafkaBootStrapServers = "localhost:9092";
	private final String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private final String producerClientId = this.getClass().getSimpleName();
	private BrokerProducerServiceFactory<String> brokerProducerServiceFactory;

	@BeforeEach
	public void setUp() throws Exception {
		List<BrokerProducerService<String>> brokerProducerServices = new ArrayList<>();
		brokerProducerServices.add(new KafkaProducerService<String, String>(kafkaBootStrapServers, keySerializerClass,
				valueSerializerClass, producerClientId));
		brokerProducerServices.add(new ActiveMQProducerService(brokerURL));
		brokerProducerServiceFactory = new BrokerProducerServiceFactoryImpl<>(brokerProducerServices);
	}

	@Test
	public void testGetBrokerProducerService() {
		assertTrue(brokerProducerServiceFactory.getBrokerProducerService("activemq") instanceof ActiveMQProducerService,
				"Expected and actual BrokerProducerService are not same");
		assertTrue(brokerProducerServiceFactory.getBrokerProducerService("kafka") instanceof KafkaProducerService,
				"Expected and actual BrokerProducerService are not same");
	}

}
