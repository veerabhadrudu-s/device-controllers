package com.hpe.broker.service.consumer.kafka;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;

import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.broker.utility.UtilityLogger;

/**
 * @author sveera
 *
 */
public class KafkaConsumerService<K, V> implements BrokerConsumerService<V> {
	private final Logger logger = getLogger(getClass());

	private final String kafkaBootStrapServers;
	private final String keyDeSerializerClass;
	private final String valueDeSerializerClass;
	private final String consumerGroupId;
	private final ExecutorService executorService;
	private final List<KafkaConsumerRunnable> KafkaConsumers = new CopyOnWriteArrayList<>();

	public KafkaConsumerService(String kafkaBootStrapServers, String keyDeSerializerClass,
			String valueDeSerializerClass, String consumerGroupId, ExecutorService executorService) {
		super();
		this.kafkaBootStrapServers = kafkaBootStrapServers;
		this.keyDeSerializerClass = keyDeSerializerClass;
		this.valueDeSerializerClass = valueDeSerializerClass;
		this.consumerGroupId = consumerGroupId;
		this.executorService = executorService;
	}

	@Override
	public String getName() {
		return "kafka";
	}

	@Override
	public void startService() {

	}

	@Override
	public void stopService() {
		for (KafkaConsumerRunnable kafkaConsumerRunnable : KafkaConsumers)
			kafkaConsumerRunnable.stopKafkaConsumerRunnable();
	}

	@Override
	public void consumeData(String destination, BrokerConsumerDataHandler<V> brokerConsumerDataHandler) {
		KafkaConsumerRunnable kafkaConsumerRunnable = new KafkaConsumerRunnable(destination, brokerConsumerDataHandler);
		KafkaConsumers.add(kafkaConsumerRunnable);
		logger.debug(
				"Trying to start new " + KafkaConsumerRunnable.class.getSimpleName() + " : " + kafkaConsumerRunnable);
		executorService.submit(kafkaConsumerRunnable);
	}

	private class KafkaConsumerRunnable implements Runnable {
		private final Logger logger = getLogger(getClass());
		private final String auto_commit_offset = "false";
		private final String auto_commit_interval_ms = "1000";
		private final int polling_interval = 100;
		private final String destination;
		private final BrokerConsumerDataHandler<V> brokerConsumerDataHandler;
		private volatile boolean isConsumerRunnable;
		private KafkaConsumer<K, V> kafkaConsumer;

		public KafkaConsumerRunnable(String destination, BrokerConsumerDataHandler<V> brokerConsumerDataHandler) {
			this.destination = destination;
			this.brokerConsumerDataHandler = brokerConsumerDataHandler;
		}

		@Override
		public void run() {
			try {
				tryInstantiatingKafkaConsumer();
				while (isConsumerRunnable) {
					ConsumerRecords<K, V> records = kafkaConsumer.poll(polling_interval);
					for (ConsumerRecord<K, V> consumerRecord : records)
						tryProcessingMessage(consumerRecord);
					kafkaConsumer.commitSync();
					// kafkaConsumer.unsubscribe();
				}
			} finally {
				closingKafkaConsumer();
			}
		}

		private void tryProcessingMessage(ConsumerRecord<K, V> consumerRecord) {
			try {
				brokerConsumerDataHandler.handleConsumerMessage(consumerRecord.value());
			} catch (Throwable e) {
				UtilityLogger.logExceptionStackTrace(e, getClass());
			}
		}

		public void stopKafkaConsumerRunnable() {
			isConsumerRunnable = false;
			if (kafkaConsumer != null)
				kafkaConsumer.wakeup();
		}

		private void tryInstantiatingKafkaConsumer() {
			try {
				initKafkaConsumer();
				kafkaConsumer.subscribe(Collections.singletonList(destination));
				isConsumerRunnable = true;
			} catch (Exception e) {
				throw new RuntimeException("Failed to start " + this.getClass().getSimpleName());
			}
		}

		private void initKafkaConsumer() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
			final Properties properties = new Properties();
			properties.setProperty("bootstrap.servers", kafkaBootStrapServers);
			properties.setProperty("group.id", consumerGroupId);
			properties.setProperty("auto.commit.interval.ms", auto_commit_interval_ms);
			properties.setProperty("auto.commit.offset", auto_commit_offset);

			final Deserializer<K> keySerializer = keyDeSerializerClass == null || keyDeSerializerClass.isEmpty()
					? (Deserializer<K>) new StringDeserializer()
					: (Deserializer<K>) Class.forName(keyDeSerializerClass).newInstance();
			final Deserializer<V> valueSerializer = valueDeSerializerClass == null || valueDeSerializerClass.isEmpty()
					? (Deserializer<V>) new StringDeserializer()
					: (Deserializer<V>) Class.forName(valueDeSerializerClass).newInstance();
			kafkaConsumer = new KafkaConsumer<>(properties, keySerializer, valueSerializer);
			logger.trace("Kafka Consumer Instance been created " + kafkaConsumer);
		}

		private void closingKafkaConsumer() {
			if (kafkaConsumer != null)
				kafkaConsumer.close();
			logger.trace("Closed " + this.toString() + " successfully.");
		}

		@Override
		public String toString() {
			return "KafkaConsumerRunnable [destination=" + destination + ", brokerConsumerDataHandler="
					+ brokerConsumerDataHandler + "]";
		}
	}
}
