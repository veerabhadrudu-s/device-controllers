package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.util.Arrays;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.mqtt.southbound.security.SecurityLayer;

/**
 * @author sveera
 *
 */
public class MqttSubscriptionService implements DeviceModelMqttSubscriptionService {

	private static final String GENERIC_MQTTDC_CLIENT_ID = "generic-mqttdc-subscriber";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String mqttBrokerUrl;
	private final SecurityLayer securityLayer;
	private final DeviceModelFactory deviceModelFactory;
	private final MqttSubscriptionMessageHandler mqttSubscriptionMessageHandler;
	private final MqttMessageHandlerService mqttMessageHandlerService;
	private final MqttClientPersistence persistence = new MemoryPersistence();
	private MqttClient mqttClient;

	public MqttSubscriptionService(String mqttBrokerUrl, SecurityLayer securityLayer,
			DeviceModelFactory deviceModelFactory, MqttMessageHandlerService mqttMessageHandlerService) {
		super();
		this.mqttBrokerUrl = mqttBrokerUrl;
		this.securityLayer = securityLayer;
		this.deviceModelFactory = deviceModelFactory;
		this.mqttMessageHandlerService = mqttMessageHandlerService;
		this.mqttSubscriptionMessageHandler = new MqttSubscriptionMessageHandler(mqttMessageHandlerService);
	}

	public void startService() {
		try {
			logger.info("Connecting to Mqtt Brocker with url " + mqttBrokerUrl);
			mqttClient = new MqttClient(mqttBrokerUrl, GENERIC_MQTTDC_CLIENT_ID, persistence);
			subscribeToTopics();
			mqttMessageHandlerService.startService();
		} catch (Exception e) {
			logger.error("Mqtt client failed to conenct : " + e.getMessage());
			logExceptionStackTrace(e, getClass());
		}

	}

	public void stopService() {
		try {
			mqttMessageHandlerService.stopService();
			mqttClient.unsubscribe(getAllTopicNamesForDeviceModels());
			mqttClient.disconnect();
			mqttClient.close();
			logger.info("MQTT client closed in subscription service");
		} catch (Throwable e) {
			logger.error("Failed to close mqtt client");
			logExceptionStackTrace(e, getClass());
		}
	}

	@Override
	public void subscribeForDeviceModel(final DeviceModel deviceModel) throws MqttException {
		String uplinkTopicFilter = constructUplinkTopicForDeviceModel(deviceModel);
		mqttClient.subscribe(uplinkTopicFilter);
		logger.info("Device model " + deviceModel + " with mqtt topic filter " + uplinkTopicFilter
				+ " been successfully subscribed");
	}

	@Override
	public void unsubscribeForDeviceModel(final DeviceModel deviceModel) throws MqttException {
		String uplinkTopicFilter = constructUplinkTopicForDeviceModel(deviceModel);
		mqttClient.unsubscribe(uplinkTopicFilter);
		logger.info("Device model " + deviceModel + " with mqtt topic filter " + uplinkTopicFilter
				+ " been successfully unsubscribed");
	}

	private void subscribeToTopics() throws Exception {
		MqttConnectOptions connect = new MqttConnectOptions();
		connect.setCleanSession(true);
		if (securityLayer.isSecurityEnabled())
			connect.setSocketFactory(securityLayer.getSSLSocketFactory());
		mqttClient.connect(connect);
		mqttClient.setCallback(mqttSubscriptionMessageHandler);
		String[] topics = getAllTopicNamesForDeviceModels();
		mqttClient.subscribe(topics);
		logger.info("Mqtt Client in subscription service connected successfully for topics " + Arrays.toString(topics));
	}

	private String[] getAllTopicNamesForDeviceModels() {
		List<DeviceModel> deviceModels = deviceModelFactory.getAllDeviceModels();
		String[] topics = new String[deviceModels.size()];
		for (int i = 0; i < deviceModels.size(); i++)
			topics[i] = constructUplinkTopicForDeviceModel(deviceModels.get(i));
		return topics;
	}

	private String constructUplinkTopicForDeviceModel(DeviceModel deviceModel) {
		return deviceModel.getManufacturer() + "/" + deviceModel.getModelId() + "/" + deviceModel.getVersion() + "/Up"
				+ "/+";
	}

	private class MqttSubscriptionMessageHandler implements MqttCallback {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final MqttMessageHandlerService mqttMessageHandlerService;

		public MqttSubscriptionMessageHandler(MqttMessageHandlerService mqttMessageHandlerService) {
			super();
			this.mqttMessageHandlerService = mqttMessageHandlerService;
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			mqttMessageHandlerService.processMqttData(new ReceivedMqttMessage(topic, message.getPayload()));
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			logger.debug("Client delivery completed. Ending client session");

		}

		@Override
		public void connectionLost(Throwable cause) {
			logMQTTException(cause);
			tryReconnectingToTopics(cause);
		}

		private void logMQTTException(Throwable cause) {
			if (cause instanceof MqttException) {
				logger.debug("Reason Code for mqtt exception " + ((MqttException) cause).getReasonCode());
				logExceptionStackTrace(cause, getClass());
			}
		}

		private void tryReconnectingToTopics(Throwable cause) {
			logger.debug("Connection lost to the broker reconnecting..........");
			if (!mqttClient.isConnected())
				try {
					subscribeToTopics();
					logger.debug("Mqtt client reconnected in subscription service");
				} catch (Exception e) {
					logger.error("Reconnection to the broker failed  " + e.getMessage());
					logExceptionStackTrace(cause, getClass());
				}
		}

	}

}
