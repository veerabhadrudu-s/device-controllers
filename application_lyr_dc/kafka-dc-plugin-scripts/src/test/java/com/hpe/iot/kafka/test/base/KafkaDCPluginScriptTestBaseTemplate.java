/**
 * 
 */
package com.hpe.iot.kafka.test.base;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.activemq.ActiveMQConsumerService;
import com.hpe.broker.service.producer.kafka.KafkaProducerService;
import com.hpe.iot.kafka.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.kafka.northbound.sdk.handler.mock.MockNorthboundUplinkConsumerService;

/**
 * @author sveera
 *
 */
public abstract class KafkaDCPluginScriptTestBaseTemplate {

	private static final int PAYLOAD_PROCESSING_WAIT_TIME = 10000;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final JsonParser jsonParser = new JsonParser();
	private static ClassPathXmlApplicationContext applicationContext;

	@BeforeAll
	public static void beforeClass() throws InterruptedException {
		applicationContext = new ClassPathXmlApplicationContext("/bean-config.xml");
		waitForKafkaInitialization();
	}

	private static void waitForKafkaInitialization() throws InterruptedException {
		Thread.sleep(5000);
	}

	@AfterAll
	public static void afterClass() {
		applicationContext.close();
	}

	protected IOTDevicePayloadHolder tryPublishingUplinkMessage(String topicName, String... uplinkPayloads) {
		return tryPublishingUplinkMessage(topicName, uplinkPayloads.length, uplinkPayloads);
	}

	protected IOTDevicePayloadHolder tryPublishingUplinkMessage(String topicName, int expectedUplinkMessagesCount,
			String... uplinkPayloads) {
		CountDownLatch countDownLatch = new CountDownLatch(expectedUplinkMessagesCount);
		IOTDevicePayloadHolder iotDevicePayloadHolder = new IOTDevicePayloadHolder(countDownLatch);
		BrokerConsumerService<String> brokerConsumerService = new ActiveMQConsumerService(
				getSpringPropertyFileValue("${activemq.brokerURL}"), getSpringPropertyFileValue("${activemq.usename}"),
				getSpringPropertyFileValue("${activemq.password}"));

		MockNorthboundUplinkConsumerService mockNorthboundUplinkConsumerService = new MockNorthboundUplinkConsumerService(
				getSpringPropertyFileValue("${iot.device.uplink.destination}"), brokerConsumerService,
				iotDevicePayloadHolder);
		try {
			KafkaProducerService<String, String> kafkaProducerService = (KafkaProducerService<String, String>) applicationContext
					.getBean("kafkaDevicePublisherService", KafkaProducerService.class);
			mockNorthboundUplinkConsumerService.startService();
			for (String uplinkPayload : uplinkPayloads)
				kafkaProducerService.publishData(topicName, uplinkPayload);
			countDownLatch.await(PAYLOAD_PROCESSING_WAIT_TIME, MILLISECONDS);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		} finally {
			mockNorthboundUplinkConsumerService.stopService();
		}
		return iotDevicePayloadHolder;
	}

	private String getSpringPropertyFileValue(String propertyName) {
		return applicationContext.getBeanFactory().resolveEmbeddedValue(propertyName);
	}

	protected String formUplinkTopicName(String manufacturer, String model, String version) {
		return formTopicName(manufacturer, model, version, "Up");

	}

	protected String formDownlinkTopicName(String manufacturer, String model, String version) {
		return formTopicName(manufacturer, model, version, "Down");

	}

	private String formTopicName(String manufacturer, String model, String version, String flow) {
		return manufacturer + "_" + model + "_" + version + "_" + flow;
	}

}
