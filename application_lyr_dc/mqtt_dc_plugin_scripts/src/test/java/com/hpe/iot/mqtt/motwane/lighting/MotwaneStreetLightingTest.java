/**
 * 
 */
package com.hpe.iot.mqtt.motwane.lighting;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;
import com.hpe.iot.mqtt.southbound.service.outflow.MqttDevicePayloadHolder;
import com.hpe.iot.mqtt.test.constants.TestConstants;
import com.hpe.iot.utility.UtilityLogger;

import static org.junit.Assert.fail;

/**
 * @author sveera
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public class MotwaneStreetLightingTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JsonParser jsonParser = new JsonParser();
	private final MqttClientPersistence persistence = new MemoryPersistence();
	private MqttClient mqttClient;

	@Value("${mqtt.broker.url}")
	private String mqttServerUrl;
	@Autowired
	private MqttDevicePayloadHolder mqttDevicePayloadHolder;

	@Before
	public void beforeTest() throws InterruptedException, MqttException {
		waitForDCInitialization();
		mqttClient = new MqttClient(mqttServerUrl, "testcase-client", persistence);
		mqttClient.connect();
	}

	@Test
	public void testMqttSouthboundServiceForMotwaneStreetLightingForALAMessageType() {
		JsonObject payload = getMotwaneStreetLightingForALAMessageType();
		tryPublishingMessage(TestConstants.MOTWANE  + "/" + TestConstants.MOTWANE_MODEL + "/" + "Up" + "/"
				+ payload.get("deviceId").getAsString(), payload);
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForGSSMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForGSSMessageType();
		tryPublishingMessage(TestConstants.MOTWANE + "/" + TestConstants.MOTWANE_MODEL + "/" + "Up" + "/"
				+ payload.get("deviceId").getAsString(), payload);
		ReceivedMqttMessage receivedMqttMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		String mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(mqttMessage).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		Assert.assertEquals(
				"Expected downlink topic and actual downlink topic are not same", TestConstants.MOTWANE  + "/"
						+ TestConstants.MOTWANE_MODEL + "/" + "Down" + "/" + payload.get("deviceId").getAsString(),
				mqttDownlinkTopic);
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForCOAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForCOAMessageType();
		tryPublishingMessage(TestConstants.MOTWANE  + "/" + TestConstants.MOTWANE_MODEL + "/" + "Up" + "/"
				+ payload.get("deviceId").getAsString(), payload);
		ReceivedMqttMessage receivedMqttMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		String mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(mqttMessage).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		Assert.assertEquals(
				"Expected downlink topic and actual downlink topic are not same", TestConstants.MOTWANE  + "/"
						+ TestConstants.MOTWANE_MODEL + "/" + "Down" + "/" + payload.get("deviceId").getAsString(),
				mqttDownlinkTopic);
	}

	private void tryPublishingMessage(String topicName, JsonObject payload) {
		MqttMessage mqttMessage = new MqttMessage(payload.toString().getBytes());
		try {
			mqttClient.publish(topicName, mqttMessage);
			waitForDCToCompletePayloadProcessing();
		} catch (Throwable e) {
			UtilityLogger.logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		}
	}

	private JsonObject getMotwaneStreetLightingForALAMessageType() {
		return jsonParser
				.parse("{\"switchInternalId\": \"S000003\", \"alertId\": \"2\", \"eventTemplateId\": \"934aa542-8256-413f-8eab-a90b1ba47883\", \"logDateTime\": \"20-07-2017 03:43:34\", \"alertMessage\": \"Phase R, Over Voltage condition at Techm, Voltage, 255.0\", \"deviceId\": \"S000003\", \"messageId\": \"ALA\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForGSSMessageType() {
		return jsonParser
				.parse("{\"eventTemplateId\": \"afb5ab4c-0d2a-422e-b9e2-efad2cc5166e\", \"encodingType\": \"BASE64\", \"message\": \"eyJzd2l0Y2hJbnRlcm5hbElkIjogIlMwMDAwMDMiLCAibGF0aXR1ZGUiOiAiMTkuOTU4MzQ3IiwgImxvbmdpdHVkZSI6ICI3My44MzUzMyIsICJsb2dEYXRlVGltZSI6ICIyMC0wNy0yMDE3IDAzOjQ0OjM1In0=\", \"deviceId\": \"S000003\", \"messageId\": \"GSS\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForCOAMessageType() {
		return jsonParser
				.parse("{\"switchInternalId\": \"S000003\", \"mobile\": \"testing\", \"eventTemplateId\": \"e1980a2d-51ba-4e5d-9618-1e63f2cd1711\", \"logDateTime\": \"09-08-2017 01:58:54\", \"deviceId\": \"S000003\", \"messageId\": \"COA\", \"IPAddress\": \"\"}")
				.getAsJsonObject();
	}

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(3000);
	}

	private void waitForDCToCompletePayloadProcessing() throws InterruptedException {
		Thread.sleep(5000);
	}
}
