/**
 * 
 */
package com.hpe.iot.mqtt.test.base;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.activemq.ActiveMQConsumerService;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.MockNorthboundDownlinkProducerService;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.MockNorthboundUplinkConsumerService;
import com.hpe.iot.mqtt.southbound.security.SecurityLayer;
import com.hpe.iot.mqtt.southbound.service.outflow.MqttDevicePayloadHolder;
import com.hpe.iot.mqtt.southbound.service.outflow.MqttDeviceSubscriptionService;

/**
 * @author sveera
 *
 */
public abstract class MqttBaseTestTemplate {

	private static final long WAIT_TIME_FOR_DC_PAYLOAD_PROCESSING = 10000;
	private static ClassPathXmlApplicationContext applicationContext;
	private MqttClient mqttClient;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final JsonParser jsonParser = new JsonParser();
	protected final MqttClientPersistence persistence = new MemoryPersistence();

	@BeforeAll
	public static void beforeClass() throws InterruptedException {
		applicationContext = new ClassPathXmlApplicationContext("/bean-config.xml");
	}

	@AfterAll
	public static void afterClass() {
		applicationContext.close();
	}

	@BeforeEach
	public void setUpTest() throws InterruptedException, MqttException {
		mqttClient = new MqttClient(applicationContext.getBeanFactory().resolveEmbeddedValue("${mqtt.broker.url}"),
				"testcase-client", persistence);
		mqttClient.connect();
	}

	@AfterEach
	public void tearDownTest() throws MqttException {
		mqttClient.disconnect();
	}

	protected IOTDevicePayloadHolder tryPublishingUplinkMessages(String topicName, byte[] payload) {
		UplinkMqttMessages uplinkMqttMessages = new UplinkMqttMessages(topicName);
		uplinkMqttMessages.addPayload(payload);
		return tryPublishingUplinkMessages(uplinkMqttMessages);
	}

	protected IOTDevicePayloadHolder tryPublishingUplinkMessages(final UplinkMqttMessages uplinkMqttMessages) {
		CountDownLatch countDownLatch = new CountDownLatch(uplinkMqttMessages.getPayloads().size());
		IOTDevicePayloadHolder iotDevicePayloadHolder = new IOTDevicePayloadHolder(countDownLatch);
		MockNorthboundUplinkConsumerService mockNorthboundUplinkConsumerService = constructMockNorthboundUplinkConsumerService(
				iotDevicePayloadHolder);
		try {
			mockNorthboundUplinkConsumerService.startService();
			for (byte[] payload : uplinkMqttMessages.getPayloads())
				mqttClient.publish(uplinkMqttMessages.getTopicName(), new MqttMessage(payload));
			countDownLatch.await(WAIT_TIME_FOR_DC_PAYLOAD_PROCESSING, MILLISECONDS);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		} finally {
			mockNorthboundUplinkConsumerService.stopService();
		}
		return iotDevicePayloadHolder;
	}

	private MockNorthboundUplinkConsumerService constructMockNorthboundUplinkConsumerService(
			IOTDevicePayloadHolder iotDevicePayloadHolder) {
		BrokerConsumerService<String> brokerConsumerService = new ActiveMQConsumerService(
				getSpringPropertyFileValue("${activemq.brokerURL}"), getSpringPropertyFileValue("${activemq.usename}"),
				getSpringPropertyFileValue("${activemq.password}"));
		MockNorthboundUplinkConsumerService mockNorthboundUplinkConsumerService = new MockNorthboundUplinkConsumerService(
				getSpringPropertyFileValue("${iot.device.uplink.destination}"), brokerConsumerService,
				iotDevicePayloadHolder);
		return mockNorthboundUplinkConsumerService;
	}

	protected MqttDevicePayloadHolder tryPublishingDownlinkMessages(final List<String> downlinkMessages) {
		CountDownLatch countDownLatch = new CountDownLatch(downlinkMessages.size());
		MqttDevicePayloadHolder mqttDevicePayloadHolder = new MqttDevicePayloadHolder(countDownLatch);
		MqttDeviceSubscriptionService mqttDeviceSubscriptionService = new MqttDeviceSubscriptionService(
				getSpringPropertyFileValue("${mqtt.broker.url}"), applicationContext.getBean(SecurityLayer.class),
				applicationContext.getBean(DeviceModelFactory.class), mqttDevicePayloadHolder);
		try {
			mqttDeviceSubscriptionService.startService();
			for (String downlinkMessage : downlinkMessages)
				applicationContext.getBean(MockNorthboundDownlinkProducerService.class)
						.publishDownlinkData(downlinkMessage);
			countDownLatch.await(WAIT_TIME_FOR_DC_PAYLOAD_PROCESSING, MILLISECONDS);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		} finally {
			mqttDeviceSubscriptionService.stopService();
		}
		return mqttDevicePayloadHolder;
	}

	protected MqttDevicePayloadHolder tryPublishingUplinMessagesForAcknowledgement(String topicName,
			byte[] uplinkpayload) {
		return tryPublishingUplinMessagesForAcknowledgement(topicName, uplinkpayload, 1);
	}

	protected MqttDevicePayloadHolder tryPublishingUplinMessagesForAcknowledgement(String topicName,
			byte[] uplinkpayload, int expectedAcknowledgementMessagesCount) {
		UplinkMqttMessages uplinkMqttMessages = new UplinkMqttMessages(topicName);
		uplinkMqttMessages.addPayload(uplinkpayload);
		return tryPublishingUplinMessagesForAcknowledgement(uplinkMqttMessages, expectedAcknowledgementMessagesCount);
	}

	protected MqttDevicePayloadHolder tryPublishingUplinMessagesForAcknowledgement(
			final UplinkMqttMessages uplinkMqttMessages, int expectedAcknowledgementMessagesCount) {
		CountDownLatch countDownLatch = new CountDownLatch(expectedAcknowledgementMessagesCount);
		MqttDevicePayloadHolder mqttDevicePayloadHolder = new MqttDevicePayloadHolder(countDownLatch);
		MqttDeviceSubscriptionService mqttDeviceSubscriptionService = new MqttDeviceSubscriptionService(
				getSpringPropertyFileValue("${mqtt.broker.url}"), applicationContext.getBean(SecurityLayer.class),
				applicationContext.getBean(DeviceModelFactory.class), mqttDevicePayloadHolder);
		try {
			mqttDeviceSubscriptionService.startService();
			for (byte[] payload : uplinkMqttMessages.getPayloads())
				mqttClient.publish(uplinkMqttMessages.getTopicName(), new MqttMessage(payload));
			countDownLatch.await(WAIT_TIME_FOR_DC_PAYLOAD_PROCESSING, MILLISECONDS);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		} finally {
			mqttDeviceSubscriptionService.stopService();
		}
		return mqttDevicePayloadHolder;
	}

	private String getSpringPropertyFileValue(String propertyName) {
		return applicationContext.getBeanFactory().resolveEmbeddedValue(propertyName);
	}

	protected String formUplinkTopicName(String manufacturer, String model, String version, String deviceId) {
		return formTopicName(manufacturer, model, version, deviceId, "Up");
	}

	protected String formDownlinkTopicName(String manufacturer, String model, String version, String deviceId) {
		return formTopicName(manufacturer, model, version, deviceId, "Down");

	}

	private String formTopicName(String manufacturer, String model, String version, String deviceId, String flow) {
		return manufacturer + "/" + model + "/" + version + "/" + flow + "/" + deviceId;
	}

	protected void validateDeviceModel(Device device, String expectedManufacturer, String expectedModelId,
			String expectedVersion, String expectedDeviceId) {
		assertEquals(expectedManufacturer, device.getManufacturer(), "Expected and Actual Manufacturer are not same");
		assertEquals(expectedModelId, device.getModelId(), "Expected and Actual Model are not same");
		assertEquals(expectedVersion, device.getVersion(), "Expected and Actual Version are not same");
		assertEquals(expectedDeviceId, device.getDeviceId(), "Expected and Actual DeviceId are not same");
	}

	protected class UplinkMqttMessages {

		private final String topicName;
		private final List<byte[]> payloads = new ArrayList<>();

		public UplinkMqttMessages(String topicName) {
			super();
			this.topicName = topicName;
		}

		public String getTopicName() {
			return topicName;
		}

		public List<byte[]> getPayloads() {
			return payloads;
		}

		public void addPayload(byte[] payload) {
			this.payloads.add(payload);
		}

	}
}
