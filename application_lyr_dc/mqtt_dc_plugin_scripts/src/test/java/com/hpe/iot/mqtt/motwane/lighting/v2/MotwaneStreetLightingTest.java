/**
 * 
 */
package com.hpe.iot.mqtt.motwane.lighting.v2;

import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.MOTWANE_VERSION_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Base64;
import java.util.Base64.Encoder;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;
import com.hpe.iot.mqtt.test.base.MqttBaseTestTemplate;

/**
 * @author sveera
 *
 */
public class MotwaneStreetLightingTest extends MqttBaseTestTemplate {

	private static final String DEVICE_ID = "S000001";
	private final Encoder encoder = Base64.getMimeEncoder();

	@Test
	public void testMqttSouthboundServiceForMotwaneStreetLightingForALAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForALAMessageType();
		tryPublishingMessage(formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_2,
				payload.get("SwitchInternalId").getAsString()), encoder.encode(payload.toString().getBytes()));
		DeviceInfo deviceInfo = iotDevicePayloadHolder.getIOTDeviceData();
		assertNotNull("Device info cannot be null", deviceInfo);
		validateDeviceModel(deviceInfo.getDevice(), MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_2, DEVICE_ID);
		logger.debug("Received Device Info is " + deviceInfo);
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForGSSMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForGSSMessageType();
		tryPublishingMessage(formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_2,
				payload.get("SwitchInternalId").getAsString()), encoder.encode(payload.toString().getBytes()));
		ReceivedMqttMessage receivedMqttMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		validateDownlinkMessage(payload.get("SwitchInternalId").getAsString(), receivedMqttMessage);
	}

	@Test
	@Ignore
	public void testMqttSouthboundServiceForMotwaneStreetLightingForCOAMessageType() throws InterruptedException {
		JsonObject payload = getMotwaneStreetLightingForCOAMessageType();
		String uplinkTopicName = formUplinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_2,
				payload.get("SwitchInternalId").getAsString());
		tryPublishingMessage(uplinkTopicName, encoder.encode(payload.toString().getBytes()));
		ReceivedMqttMessage downlinkSYDMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		validateDownlinkMessage(payload.get("SwitchInternalId").getAsString(), downlinkSYDMessage);
		ReceivedMqttMessage downlinkSPSMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		validateDownlinkMessage(payload.get("SwitchInternalId").getAsString(), downlinkSPSMessage);
		ReceivedMqttMessage downlinkSSSMessage = mqttDevicePayloadHolder.getMqttDeviceData();
		validateDownlinkMessage(payload.get("SwitchInternalId").getAsString(), downlinkSSSMessage);
	}

	private void validateDownlinkMessage(String expectedDeviceId, ReceivedMqttMessage receivedMqttMessage) {
		String mqttDownlinkTopic = receivedMqttMessage.getMqttTopic();
		byte[] mqttMessage = receivedMqttMessage.getMqttMessage();
		JsonObject downlinkPayload = jsonParser.parse(new String(mqttMessage)).getAsJsonObject();
		logger.debug("Received downlink message is " + downlinkPayload.toString());
		assertEquals("Expected downlink topic and actual downlink topic are not same",
				formDownlinkTopicName(MOTWANE, MOTWANE_MODEL, MOTWANE_VERSION_2, expectedDeviceId), mqttDownlinkTopic);
		assertEquals("Expected and Actual DeviceId are not same", expectedDeviceId,
				downlinkPayload.get("SwitchInternalId").getAsString());
	}

	private JsonObject getMotwaneStreetLightingForALAMessageType() {
		return jsonParser.parse(
				"{\"Event\" :\"ALA\", \"SwitchInternalId\":\"S000001\",\"AlertId\":\"2\",\"SwitchDateTime\":\"11-07-2016 11:34:25\", \"Msg\":\"Hello\", \"Mobile\":\"9654123562,2547896321\", \"Email\":\"s@s.in,a@a.in\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForGSSMessageType() {
		return jsonParser.parse(
				"{\"Event\" :\"GSS\", \"SwitchInternalId\":\"S000001\", \"SwitchDateTime\":\"11-07-2016 11:34:25\",\"Latitude\":\"30.1523\",\"Longitude\":\"73.1256\"}")
				.getAsJsonObject();
	}

	private JsonObject getMotwaneStreetLightingForCOAMessageType() {
		return jsonParser.parse(
				"{\"Event\":\"COA\",\"SwitchInternalId\":\"S000001\",\"SwitchDateTime\":\"25-07-2016 03:05:15\", \"Mobile\":\"1012314562\", \"IPAddress\":\"192.168.0.168\", \"Latitude\":\"70.1235\",\"Longitude\":\"43.1256\",\"GPSRealTime\":\"25-07-2016 03:05:15\"}")
				.getAsJsonObject();
	}

}
