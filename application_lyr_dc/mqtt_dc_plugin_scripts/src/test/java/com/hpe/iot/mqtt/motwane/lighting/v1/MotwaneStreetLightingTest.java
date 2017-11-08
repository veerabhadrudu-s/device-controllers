/**
 * 
 */
package com.hpe.iot.mqtt.motwane.lighting.v1;

import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_VERSION_1;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;
import com.hpe.iot.mqtt.test.base.MqttBaseTestTemplate;

/**
 * @author sveera
 *
 */
public class MotwaneStreetLightingTest extends MqttBaseTestTemplate {

	@Test
	public void testMqttSouthboundServiceForMotwaneStreetLightingForALAMessageType() {
		JsonObject payload = getMotwaneStreetLightingForALAMessageType();
		tryPublishingMessage(formUplinkTopicName(MOTWANE_MODEL, MOTWANE_MODEL, MOTWANE_VERSION_1,
				payload.get("deviceId").getAsString()), payload.toString().getBytes());
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForGSSMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForGSSMessageType();
		tryPublishingMessage(formUplinkTopicName(MOTWANE_MODEL, MOTWANE_MODEL, MOTWANE_VERSION_1,
				payload.get("deviceId").getAsString()), payload.toString().getBytes());
		ReceivedMqttMessage receivedMqttMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		byte[] mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(new String(mqttMessage)).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		Assert.assertEquals("Expected downlink topic and actual downlink topic are not same",
				formDownlinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, payload.get("deviceId").getAsString()),
				mqttDownlinkTopic);
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForCOAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForCOAMessageType();
		tryPublishingMessage(formUplinkTopicName(MOTWANE_MODEL, MOTWANE_MODEL, MOTWANE_VERSION_1,
				payload.get("deviceId").getAsString()), payload.toString().getBytes());
		ReceivedMqttMessage receivedMqttMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		byte[] mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(new String(mqttMessage)).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		Assert.assertEquals("Expected downlink topic and actual downlink topic are not same",
				formDownlinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, payload.get("deviceId").getAsString()),
				mqttDownlinkTopic);
	}

	private JsonObject getMotwaneStreetLightingForALAMessageType() {
		return jsonParser.parse(
				"{\"switchInternalId\": \"S000003\", \"alertId\": \"2\", \"eventTemplateId\": \"934aa542-8256-413f-8eab-a90b1ba47883\", \"logDateTime\": \"20-07-2017 03:43:34\", \"alertMessage\": \"Phase R, Over Voltage condition at Techm, Voltage, 255.0\", \"deviceId\": \"S000003\", \"messageId\": \"ALA\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForGSSMessageType() {
		return jsonParser.parse(
				"{\"eventTemplateId\": \"afb5ab4c-0d2a-422e-b9e2-efad2cc5166e\", \"encodingType\": \"BASE64\", \"message\": \"eyJzd2l0Y2hJbnRlcm5hbElkIjogIlMwMDAwMDMiLCAibGF0aXR1ZGUiOiAiMTkuOTU4MzQ3IiwgImxvbmdpdHVkZSI6ICI3My44MzUzMyIsICJsb2dEYXRlVGltZSI6ICIyMC0wNy0yMDE3IDAzOjQ0OjM1In0=\", \"deviceId\": \"S000003\", \"messageId\": \"GSS\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForCOAMessageType() {
		return jsonParser.parse(
				"{\"switchInternalId\": \"S000003\", \"mobile\": \"testing\", \"eventTemplateId\": \"e1980a2d-51ba-4e5d-9618-1e63f2cd1711\", \"logDateTime\": \"09-08-2017 01:58:54\", \"deviceId\": \"S000003\", \"messageId\": \"COA\", \"IPAddress\": \"\"}")
				.getAsJsonObject();
	}

}
