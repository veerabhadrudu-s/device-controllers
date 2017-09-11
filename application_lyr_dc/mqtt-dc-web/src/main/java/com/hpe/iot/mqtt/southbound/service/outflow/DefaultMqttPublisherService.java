/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.outflow;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.hpe.iot.model.Device;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.DeviceModel;
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
		tryPublishingMessage(
				device.getManufacturer() + "/" + device.getModelId() + "/" + "Down" + "/" + device.getDeviceId(),
				deviceInfo.getPayload());
	}

	private void tryPublishingMessage(String topicName, JsonObject payload) {
		logger.debug("Received MQTT message with topic name " + topicName + " is : " + payload.toString());
		MqttMessage mqttMessage = new MqttMessage(payload.toString().getBytes());
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
