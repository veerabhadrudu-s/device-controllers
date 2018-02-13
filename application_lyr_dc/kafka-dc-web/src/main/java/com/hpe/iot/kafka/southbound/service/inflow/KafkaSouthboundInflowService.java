/**
 * 
 */
package com.hpe.iot.kafka.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.broker.service.consumer.kafka.KafkaConsumerService;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
public class KafkaSouthboundInflowService {

	private static final String GENERIC_KAFKA_DC_CONSUMER_GROUP_ID = "generic-kafkadc-consumer-group";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String kafkaBrokerUrl;
	private final DeviceModelFactory deviceModelFactory;
	private final KafkaConsumerService<String, byte[]> kafkaConsumerService;
	private final KafkaSubscriptionMessageHandler kafkaSubscriptionMessageHandler;
	private final SouthboundService southboundService;

	public KafkaSouthboundInflowService(String kafkaBrokerUrl, DeviceModelFactory deviceModelFactory,
			SouthboundService southboundService, ManagedExecutorService executorService) {
		super();
		this.kafkaBrokerUrl = kafkaBrokerUrl;
		this.deviceModelFactory = deviceModelFactory;
		this.southboundService = southboundService;
		this.kafkaConsumerService = new KafkaConsumerService<>(kafkaBrokerUrl,
				"org.apache.kafka.common.serialization.StringDeserializer",
				"org.apache.kafka.common.serialization.ByteArrayDeserializer", GENERIC_KAFKA_DC_CONSUMER_GROUP_ID,
				executorService);
		this.kafkaSubscriptionMessageHandler = new KafkaSubscriptionMessageHandler();
	}

	public void startService() {
		try {
			subscribeToTopics();
		} catch (Exception e) {
			logger.error("Kafka client failed to conenct : " + e.getMessage());
			logExceptionStackTrace(e, getClass());
		}

	}

	public void stopService() {
		try {
			kafkaConsumerService.stopService();
			logger.info("Kafka client closed in subscription service");
		} catch (Throwable e) {
			logger.error("Failed to close Kafka client");
			logExceptionStackTrace(e, getClass());
		}
	}

	private void subscribeToTopics() throws Exception {
		String[] topics = getAllTopicNamesForDeviceModels();
		logger.info("Connecting to Kafka Brocker with url " + kafkaBrokerUrl);
		kafkaConsumerService.startService();
		for (String topic : topics)
			kafkaConsumerService.consumeData(topic, kafkaSubscriptionMessageHandler);
		logger.info("Kafka Client in subscription service connected to topics " + Arrays.toString(topics));
	}

	private String[] getAllTopicNamesForDeviceModels() {
		List<DeviceModel> deviceModels = deviceModelFactory.getAllDeviceModels();
		String[] topics = new String[deviceModels.size()];
		for (int i = 0; i < deviceModels.size(); i++)
			topics[i] = deviceModels.get(i).getManufacturer() + "_" + deviceModels.get(i).getModelId() + "_"
					+ deviceModels.get(i).getVersion() + "_Up";
		return topics;
	}

	private class KafkaSubscriptionMessageHandler implements BrokerConsumerDataHandler<byte[]> {
		private final Logger logger = LoggerFactory.getLogger(getClass());

		@Override
		public void handleConsumerMessage(String topic, byte[] consumerData) {
			String topicParts[] = topic.split("_");
			String manufacturer = topicParts[0];
			String modelId = topicParts[1];
			String version = topicParts[2];
			logger.trace("Received data from Kafka topic " + topic + " with data "
					+ convertArrayOfByteToString(consumerData));
			southboundService.processPayload(manufacturer, modelId, version, consumerData);
		}
	}

}
