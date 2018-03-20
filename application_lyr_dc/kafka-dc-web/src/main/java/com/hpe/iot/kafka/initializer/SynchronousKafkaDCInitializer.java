package com.hpe.iot.kafka.initializer;

import javax.enterprise.concurrent.ManagedExecutorService;

import com.hpe.iot.kafka.southbound.service.inflow.KafkaSouthboundInflowService;
import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class SynchronousKafkaDCInitializer extends KafkaDCInitializer {

	public SynchronousKafkaDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			KafkaSouthboundInflowService kafkaSouthboundInflowService, ManagedExecutorService executorService) {
		super(jsonPathMetaModelServiceActivator, groovyScriptServiceActivator, kafkaSouthboundInflowService,
				executorService);
	}

	@Override
	public void startDC() {
		startKafkaDC();
	}

}
