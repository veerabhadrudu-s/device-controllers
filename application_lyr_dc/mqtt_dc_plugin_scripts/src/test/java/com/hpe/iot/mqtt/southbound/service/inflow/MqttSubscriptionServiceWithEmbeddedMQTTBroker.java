/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.mqtt.southbound.security.SecurityLayer;

/**
 * @author sveera
 *
 */
public class MqttSubscriptionServiceWithEmbeddedMQTTBroker extends MqttSubscriptionService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String mqttBrokerURI;
	private BrokerService brokerService;

	public MqttSubscriptionServiceWithEmbeddedMQTTBroker(String mqttBrokerUrl, SecurityLayer securityLayer,
			DeviceModelFactory deviceModelFactory, MqttMessageHandlerService mqttMessageHandlerService,
			String mqttBrokerURI) {
		super(mqttBrokerUrl, securityLayer, deviceModelFactory, mqttMessageHandlerService);
		this.mqttBrokerURI = mqttBrokerURI;
	}

	@Override
	public void startService() {
		initializeEmbeddedMqttBroker();
		super.startService();
	}

	private void initializeEmbeddedMqttBroker() {
		try {
			brokerService = new BrokerService();
			brokerService.setPersistent(false);
			brokerService.setUseJmx(false);
			brokerService.addConnector(mqttBrokerURI);
			brokerService.setBrokerName("mqtt-broker");
			brokerService.setUseShutdownHook(false);
			brokerService.start();
			logger.info("MQTT Broker started successfully");
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			throw new RuntimeException("Failed to start embedded MQTT Broker");
		}
	}

	@Override
	public void stopService() {
		super.stopService();
		try {
			brokerService.stop();
		} catch (Exception e) {
			logExceptionStackTrace(e, getClass());
			throw new RuntimeException("Failed to stop embedded MQTT Broker");
		}
	}

}
