package com.hpe.iot.kafka.initializer;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.initializer.DCInitializer;
import com.hpe.iot.kafka.southbound.service.inflow.KafkaSouthboundInflowService;
import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class KafkaDCInitializer implements DCInitializer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator;
	private final GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator;
	private final KafkaSouthboundInflowService kafkaSouthboundInflowService;
	private final ManagedExecutorService executorService;

	public KafkaDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			KafkaSouthboundInflowService kafkaSouthboundInflowService, ManagedExecutorService executorService) {
		super();
		this.jsonPathMetaModelServiceActivator = jsonPathMetaModelServiceActivator;
		this.groovyScriptServiceActivator = groovyScriptServiceActivator;
		this.kafkaSouthboundInflowService = kafkaSouthboundInflowService;
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
					kafkaSouthboundInflowService.startService();
					logger.info("Kafka DC Initialization completed successfully");
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
			kafkaSouthboundInflowService.stopService();
		} catch (Exception e) {
			logger.error("Failed to stop DC services ");
			logExceptionStackTrace(e, getClass());
		}
	}

}
