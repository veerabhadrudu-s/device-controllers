package com.hpe.iot.mqtt.initializer;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.initializer.DCInitializer;
import com.hpe.iot.mqtt.southbound.service.inflow.MqttSubscriptionService;
import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class MQTTDCInitializer implements DCInitializer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator;
	private final GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator;
	private final MqttSubscriptionService mqttSubscriptionService;
	private final ManagedExecutorService executorService;

	public MQTTDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			MqttSubscriptionService mqttSubscriptionService, ManagedExecutorService executorService) {
		super();
		this.jsonPathMetaModelServiceActivator = jsonPathMetaModelServiceActivator;
		this.groovyScriptServiceActivator = groovyScriptServiceActivator;
		this.mqttSubscriptionService = mqttSubscriptionService;
		this.executorService = executorService;
	}

	@Override
	public void startDC() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					jsonPathMetaModelServiceActivator.startAllServices();
					groovyScriptServiceActivator.startAllServices();
					mqttSubscriptionService.startService();
					logger.info("MQTT DC Initialization completed successfully");
				} catch (Exception e) {
					logger.error("Failed to start DC services");
					logExceptionStackTrace(e, getClass());
				}
			}
		});
	}

	@Override
	public void stopDC() {
		try {
			jsonPathMetaModelServiceActivator.stopAllServices();
			groovyScriptServiceActivator.stopAllServices();
			mqttSubscriptionService.stopService();
		} catch (Exception e) {
			logger.error("Failed to stop DC services ");
			logExceptionStackTrace(e, getClass());
		}
	}

}
