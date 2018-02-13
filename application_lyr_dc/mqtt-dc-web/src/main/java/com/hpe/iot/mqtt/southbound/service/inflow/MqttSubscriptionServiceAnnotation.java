package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
import org.springframework.beans.factory.annotation.Value;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.mqtt.southbound.security.SecurityLayer;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
// @Service
// Enable Above Annotation to make this class to be considered during component
// scanning phase by Spring Framework.
public class MqttSubscriptionServiceAnnotation {
	private static final String GENERIC_MQTTDC_CLIENT_ID = "generic-mqttdc-client";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MqttClient mqttClient;
	private final String activeMqPublisherUrl;
	private final SecurityLayer securityLayer;
	private final DeviceModelFactory deviceModelFactory;
	private final MqttSubscriptionMessageHandler mqttSubscriptionMessageHandler;
	private final MqttClientPersistence persistence = new MemoryPersistence();

	@Inject // JSR 330 Specification
	// @Autowired // We can use Autowired annotation also.
	public MqttSubscriptionServiceAnnotation(@Value("${mqtt.broker.url}") String activeMqPublisherUrl,
			SecurityLayer securityLayer, SouthboundService southboundService, DeviceModelFactory deviceModelFactory) {
		super();
		this.activeMqPublisherUrl = activeMqPublisherUrl;
		this.securityLayer = securityLayer;
		this.deviceModelFactory = deviceModelFactory;
		this.mqttSubscriptionMessageHandler = new MqttSubscriptionMessageHandler(southboundService);
	}

	@PostConstruct
	public void initService() {
		try {
			mqttClient = new MqttClient(activeMqPublisherUrl, GENERIC_MQTTDC_CLIENT_ID, persistence);
			subscribeForTopics();
		} catch (Exception e) {
			logger.error("Mqtt client failed to conenct : " + e.getMessage());
			logExceptionStackTrace(e, getClass());
		}

	}

	private void subscribeForTopics() throws Exception {
		MqttConnectOptions connect = new MqttConnectOptions();
		connect.setCleanSession(true);
		if (securityLayer.isSecurityEnabled())
			connect.setSocketFactory(securityLayer.getSSLSocketFactory());
		mqttClient.connect(connect);
		mqttClient.setCallback(mqttSubscriptionMessageHandler);
		List<DeviceModel> deviceModels = deviceModelFactory.getAllDeviceModels();
		String[] topics = new String[deviceModels.size()];
		for (int i = 0; i < deviceModels.size(); i++)
			topics[i] = deviceModels.get(i).getManufacturer() + "/" + deviceModels.get(i).getModelId() + "/"
					+ deviceModels.get(i).getVersion() + "/Up" + "/+";
		mqttClient.subscribe(topics);
		logger.info("Mqtt Client in subscription service connected successfully for topics " + Arrays.toString(topics));
	}

	@PreDestroy
	public void closeServiceResources() {
		try {
			mqttClient.disconnect();
			mqttClient.close();
			logger.info("MQTT client closed in subscription service");
		} catch (Throwable e) {
			logger.error("Failed to close mqtt client");
			logExceptionStackTrace(e, getClass());
		}
	}

	private class MqttSubscriptionMessageHandler implements MqttCallback {
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final SouthboundService southboundService;

		public MqttSubscriptionMessageHandler(SouthboundService southboundService) {
			super();
			this.southboundService = southboundService;
		}

		@Override
		public void connectionLost(Throwable cause) {
			if (cause instanceof MqttException) {
				logger.debug("Reason Code for mqtt exception " + ((MqttException) cause).getReasonCode());
				logExceptionStackTrace(cause, getClass());
			}
			logger.debug("Connection lost to the broker reconnecting..........");
			if (!mqttClient.isConnected())
				try {
					subscribeForTopics();
					logger.debug("Mqtt client reconnected in subscription service");
				} catch (Exception e) {
					logger.error("Reconnection to the broker failed  " + e.getMessage());
					logExceptionStackTrace(cause, getClass());
				}

		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String topicParts[] = topic.split("/");
			String manufacturer = topicParts[0], modelId = topicParts[1], version = topicParts[2];
			southboundService.processPayload(manufacturer, modelId, version, message.getPayload());
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			logger.debug("Client delivery completed. Ending client session");

		}

	}

}
