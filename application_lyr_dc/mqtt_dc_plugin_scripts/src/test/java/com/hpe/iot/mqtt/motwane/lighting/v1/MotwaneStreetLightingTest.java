/**
 * 
 */
package com.hpe.iot.mqtt.motwane.lighting.v1;

import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_VERSION_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.mqtt.northbound.sdk.handler.mock.IOTDevicePayloadHolder;
import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;
import com.hpe.iot.mqtt.southbound.service.outflow.MqttDevicePayloadHolder;
import com.hpe.iot.mqtt.test.base.MqttBaseTestTemplate;

/**
 * @author sveera
 *
 */
public class MotwaneStreetLightingTest extends MqttBaseTestTemplate {

	@Test
	public void testMqttSouthboundServiceForMotwaneStreetLightingForALAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForALAMessageType();
		IOTDevicePayloadHolder iotDevicePayloadHolder = tryPublishingUplinkMessages(
				formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, payload.get("deviceId").getAsString()),
				payload.toString().getBytes());
		List<DeviceInfo> devicePayloads = iotDevicePayloadHolder.getIOTDeviceData();
		assertNotNull(devicePayloads.get(0), "Device info cannot be null");
		validateDeviceModel(devicePayloads.get(0).getDevice(), MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1,
				payload.get("deviceId").getAsString());
		logger.debug("Received Device Info is " + devicePayloads);
	}

	@Test
	@Disabled
	public void testMqttSouthboundServiceForMotwaneStreetLightingForGSSMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForGSSMessageType();
		MqttDevicePayloadHolder mqttDevicePayloadHolder = tryPublishingUplinMessagesForAcknowledgement(
				formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, payload.get("deviceId").getAsString()),
				payload.toString().getBytes());
		List<ReceivedMqttMessage> receivedMqttMessages = mqttDevicePayloadHolder.getMqttDeviceData();
		validateDownlinkMessage(payload.get("deviceId").getAsString(), receivedMqttMessages.get(0));
	}

	@Test
	@Disabled
	public void testMqttSouthboundServiceForMotwaneStreetLightingForCOAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForCOAMessageType();
		MqttDevicePayloadHolder mqttDevicePayloadHolder = tryPublishingUplinMessagesForAcknowledgement(
				formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, payload.get("deviceId").getAsString()),
				payload.toString().getBytes(), 3);
		List<ReceivedMqttMessage> receivedMqttMessages = mqttDevicePayloadHolder.getMqttDeviceData();
		ReceivedMqttMessage downlinkSYDMessage = receivedMqttMessages.get(0);
		validateDownlinkMessage(payload.get("deviceId").getAsString(), downlinkSYDMessage);
		ReceivedMqttMessage downlinkSPSMessage = receivedMqttMessages.get(1);
		validateDownlinkMessage(payload.get("deviceId").getAsString(), downlinkSPSMessage);
		ReceivedMqttMessage downlinkSSSMessage = receivedMqttMessages.get(2);
		validateDownlinkMessage(payload.get("deviceId").getAsString(), downlinkSSSMessage);

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

	private void validateDownlinkMessage(String expectedDeviceId, ReceivedMqttMessage receivedMqttMessage) {
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		byte[] mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(new String(mqttMessage)).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		assertEquals(formDownlinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_1, expectedDeviceId),
				mqttDownlinkTopic, "Expected downlink topic and actual downlink topic are not same");
		assertEquals(expectedDeviceId, downlinkPayload.get("deviceId").getAsString(),
				"Expected and Actual DeviceId are not same");
	}

}
