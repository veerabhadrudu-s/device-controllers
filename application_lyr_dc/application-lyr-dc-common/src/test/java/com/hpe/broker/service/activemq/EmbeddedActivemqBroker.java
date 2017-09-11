package com.hpe.broker.service.activemq;


import static com.hpe.broker.utility.UtilityLogger.logExceptionStackTrace;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author sveera
 *
 */
public class EmbeddedActivemqBroker {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String brokerUrl;
	private BrokerService brokerService;

	public EmbeddedActivemqBroker(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public void startService() {
		initializeEmbeddedMqttBroker();
	}

	private void initializeEmbeddedMqttBroker() {
		try {
			brokerService = new BrokerService();
			brokerService.setPersistent(false);
			brokerService.setUseJmx(false);
			brokerService.addConnector(brokerUrl);
			brokerService.setBrokerName("activemq-message-broker");
			brokerService.setUseShutdownHook(false);
			brokerService.start();
			logger.info("ActiveMQ Broker started successfully");
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			throw new RuntimeException("Failed to start embedded ActiveMQ Broker");
		}
	}

	public void stopService() {
		try {
			brokerService.stop();
		} catch (Exception e) {
			logExceptionStackTrace(e, getClass());
			throw new RuntimeException("Failed to stop embedded ActiveMQ Broker");
		}
	}

}
