/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.outflow;

import static com.hpe.iot.utility.UtilityLogger.convertArrayOfByteToString;
import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.mqtt.southbound.security.SecurityLayer;
import com.hpe.iot.southbound.service.outflow.SouthboundPublisherService;

/**
 * @author sveera
 *
 */
public class DefaultMqttPublisherService implements SouthboundPublisherService {

	private static final String GENERIC_MQTTDC_CLIENT_ID = "generic-mqttdc-publisher";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final MqttClientPersistence persistence = new MemoryPersistence();
	private final String mqttBrokerUrl;
	private final SecurityLayer securityLayer;

	public DefaultMqttPublisherService(String mqttBrokerUrl, SecurityLayer securityLayer) {
		super();
		this.mqttBrokerUrl = mqttBrokerUrl;
		this.securityLayer = securityLayer;
	}

	@Override
	public void publishPayload(DeviceModel deviceModel, DeviceInfo deviceInfo) {
		Device device = deviceInfo.getDevice();
		publishJsonMessage(formDownlinkTopicName(device.getManufacturer(), device.getModelId(), device.getVersion(),
				device.getDeviceId()), deviceInfo.getPayload());
	}

	@Override
	public void publishPayload(DeviceModel deviceModel, String deviceId, byte[] decipheredPayload) {
		publishBinaryMessage(formDownlinkTopicName(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), deviceId), decipheredPayload);
	}

	private String formDownlinkTopicName(String manufacturer, String modelId, String version, String deviceId) {
		return manufacturer + "/" + modelId + "/" + version + "/" + "Down" + "/" + deviceId;
	}

	private void publishJsonMessage(String topicName, JsonObject payload) {
		logger.debug("Received MQTT message with topic name " + topicName + " is : " + payload.toString());
		byte[] payloadBytes = payload.toString().getBytes();
		tryPublishingMessage(topicName, payloadBytes);
	}

	private void publishBinaryMessage(String topicName, byte[] payload) {
		logger.debug(
				"Received MQTT message with topic name " + topicName + " is : " + convertArrayOfByteToString(payload));
		tryPublishingMessage(topicName, payload);
	}

	private void tryPublishingMessage(String topicName, byte[] payloadBytes) {
		MqttMessage mqttMessage = new MqttMessage(payloadBytes);
		try {
			MqttClient mqttClient = connectToMqttBroker();
			mqttClient.publish(topicName, mqttMessage);
			mqttClient.disconnectForcibly();
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
		}
	}

	public MqttClient connectToMqttBroker() throws Exception {
		logger.info("Connecting to Mqtt Brocker with url " + mqttBrokerUrl);
		MqttClient mqttClient = new MqttClient(mqttBrokerUrl, GENERIC_MQTTDC_CLIENT_ID, persistence);
		MqttConnectOptions connect = new MqttConnectOptions();
		connect.setCleanSession(true);
		if (securityLayer.isSecurityEnabled())
			connect.setSocketFactory(securityLayer.getSSLSocketFactory());
		mqttClient.connect(connect);
		return mqttClient;
	}

}
