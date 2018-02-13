/**
 * 
 */
package com.hpe.iot.mqtt.sensenuts.lighting;

import static com.hpe.iot.mqtt.sensenuts.lighting.SensenutsStreetLightingTestData.DEVICE_ID;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS_MODEL;
import static com.hpe.iot.mqtt.test.constants.TestConstants.SENSENUTS_VERSION;
import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.handson.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public class SensenutsStreetLightingCloudTest {

	private final SensenutsStreetLightingTestData sensenutsStreetLightingTestData = new SensenutsStreetLightingTestData();
	private final MqttClientPersistence persistence = new MemoryPersistence();
	private final String mqttServerUrl = "tcp://10.3.239.75:1883";
	private MqttClient mqttClient;

	@BeforeEach
	public void beforeTest() throws InterruptedException, MqttException {
		mqttClient = new MqttClient(mqttServerUrl, "testcase-client", persistence);
		mqttClient.connect();
	}

	@AfterEach
	public void afterTest() throws InterruptedException, MqttException {
		mqttClient.disconnect();
	}

	@Test
	@Disabled
	public void testSensenutsStreetLightingForUplinkNotification() {
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				sensenutsStreetLightingTestData.constructPeriodicMessage());
	}

	@Test
	@Disabled
	public void testSensenutsStreetLightingForUplinkPowerOutageAlert() throws InterruptedException {
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				sensenutsStreetLightingTestData.constructPowerOutageAlertMessage());
	}

	@Test
	@Disabled
	public void testSensenutsStreetLightingForUplinkLuminaireFailureAlert() throws InterruptedException {
		tryPublishingMessage(formUplinkTopicName(SENSENUTS, SENSENUTS_MODEL, SENSENUTS_VERSION, DEVICE_ID),
				sensenutsStreetLightingTestData.constructLuminaireFailureMessage());
	}

	protected String formUplinkTopicName(String manufacturer, String model, String version, String deviceId) {
		return formTopicName(manufacturer, model, version, deviceId, "Up");
	}

	private String formTopicName(String manufacturer, String model, String version, String deviceId, String flow) {
		return manufacturer + "/" + model + "/" + version + "/" + flow + "/" + deviceId;
	}

	private void tryPublishingMessage(String topicName, byte[] payload) {
		MqttMessage mqttMessage = new MqttMessage(payload);
		try {
			mqttClient.publish(topicName, mqttMessage);
		} catch (Throwable e) {
			UtilityLogger.logExceptionStackTrace(e, getClass());
			fail("Failed to run JUNIT Test case ");
		}
	}

}
