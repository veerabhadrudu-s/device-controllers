/**
 * 
 */
package com.hpe.iot.mqtt.test.base;

import static org.junit.Assert.fail;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.gson.JsonParser;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.MockNorthboundDownlinkProducerService;
import com.hpe.iot.mqtt.southbound.service.outflow.MqttDevicePayloadHolder;
import com.hpe.iot.utility.UtilityLogger;

/**
 * @author sveera
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public abstract class MqttBaseTestTemplate {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final JsonParser jsonParser = new JsonParser();
	protected final MqttClientPersistence persistence = new MemoryPersistence();
	protected MqttClient mqttClient;

	@Value("${mqtt.broker.url}")
	protected String mqttServerUrl;
	@Autowired
	protected MqttDevicePayloadHolder mqttDevicePayloadHolder;
	@Autowired
	protected IOTDevicePayloadHolder iotDevicePayloadHolder;
	@Autowired
	protected MockNorthboundDownlinkProducerService mockNorthboundDownlinkProducerService;

	@Before
	public void beforeTest() throws InterruptedException, MqttException {
		waitForDCInitialization();
		mqttClient = new MqttClient(mqttServerUrl, "testcase-client", persistence);
		mqttClient.connect();
	}

	protected void tryPublishingMessage(String topicName, byte[] payload) {
		MqttMessage mqttMessage = new MqttMessage(payload);
		try {
			mqttClient.publish(topicName, mqttMessage);
			waitForDCToCompletePayloadProcessing();
		} catch (Throwable e) {
			UtilityLogger.logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		}
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

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(5000);
	}

	protected void waitForDCToCompletePayloadProcessing() throws InterruptedException {
		Thread.sleep(5000);
	}
}
