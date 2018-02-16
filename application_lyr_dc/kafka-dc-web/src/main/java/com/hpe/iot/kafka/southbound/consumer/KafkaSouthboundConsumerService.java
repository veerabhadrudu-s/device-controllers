/**
 * 
 */
package com.hpe.iot.kafka.southbound.consumer;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;

/**
 * @author sveera
 *
 */
public class KafkaSouthboundConsumerService<K, V> extends KafkaConsumerService<K, V> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public KafkaSouthboundConsumerService(String kafkaBootStrapServers, String keyDeSerializerClass,
			String valueDeSerializerClass, String consumerGroupId, ExecutorService executorService) {
		super(kafkaBootStrapServers, keyDeSerializerClass, valueDeSerializerClass, consumerGroupId, executorService);
	}

	public void stopDataConsumption(String destination) {
		KafkaConsumerRunnable kafkaConsumerRunnable = this.kafkaConsumers.get(destination);
		if (kafkaConsumerRunnable != null) {
			kafkaConsumerRunnable.stopKafkaConsumerRunnable();
			logger.info("Kafka consumer service been stopped consuming messages on topic " + destination);
		}

	}

}
