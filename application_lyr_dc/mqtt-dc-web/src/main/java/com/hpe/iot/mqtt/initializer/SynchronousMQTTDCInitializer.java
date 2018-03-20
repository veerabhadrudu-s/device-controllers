package com.hpe.iot.mqtt.initializer;

import javax.enterprise.concurrent.ManagedExecutorService;

import com.hpe.iot.mqtt.southbound.service.inflow.MqttSubscriptionService;
import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class SynchronousMQTTDCInitializer extends MQTTDCInitializer {

	public SynchronousMQTTDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			MqttSubscriptionService mqttSubscriptionService, ManagedExecutorService executorService) {
		super(jsonPathMetaModelServiceActivator, groovyScriptServiceActivator, mqttSubscriptionService,
				executorService);
	}

	@Override
	public void startDC() {
		startMQTTDC();
	}

}
