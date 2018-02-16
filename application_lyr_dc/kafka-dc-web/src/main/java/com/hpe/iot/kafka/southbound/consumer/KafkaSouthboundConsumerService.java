/**
 * 
 */
package com.hpe.iot.kafka.southbound.consumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;

/**
 * @author sveera
 *
 */
public class KafkaSouthboundConsumerService<K, V> extends KafkaConsumerService<K, V> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<String, KafkaConsumerRunnable> kafkaConsumers;

	public KafkaSouthboundConsumerService(String kafkaBootStrapServers, String keyDeSerializerClass,
			String valueDeSerializerClass, String consumerGroupId, ExecutorService executorService) {
		super(kafkaBootStrapServers, keyDeSerializerClass, valueDeSerializerClass, consumerGroupId, executorService);
		this.kafkaConsumers = new ConcurrentHashMap<>();
	}

	@Override
	public void stopService() {
		for (KafkaConsumerRunnable kafkaConsumerRunnable : kafkaConsumers.values())
			kafkaConsumerRunnable.stopKafkaConsumerRunnable();
	}

	@Override
	public void consumeData(String destination, BrokerConsumerDataHandler<V> brokerConsumerDataHandler) {
		KafkaConsumerRunnable existingKafkaConsumerRunnable = this.kafkaConsumers.get(destination);
		if (existingKafkaConsumerRunnable != null)
			existingKafkaConsumerRunnable.stopKafkaConsumerRunnable();
		KafkaConsumerRunnable kafkaConsumerRunnable = new KafkaConsumerRunnable(destination, brokerConsumerDataHandler);
		kafkaConsumers.put(destination, kafkaConsumerRunnable);
		logger.debug(
				"Trying to start new " + KafkaConsumerRunnable.class.getSimpleName() + " : " + kafkaConsumerRunnable);
		executorService.submit(kafkaConsumerRunnable);
	}

	public void stopDataConsumption(String destination) {
		KafkaConsumerRunnable kafkaConsumerRunnable = this.kafkaConsumers.get(destination);
		if (kafkaConsumerRunnable != null) {
			kafkaConsumerRunnable.stopKafkaConsumerRunnable();
			logger.info("Kafka consumer service been stopped consuming messages on topic " + destination);
		}

	}

}
