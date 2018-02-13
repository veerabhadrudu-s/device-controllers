package com.hpe.iot.http.initializer;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.initializer.DCInitializer;
import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class HTTPDCInitializer implements DCInitializer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator;
	private final GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator;
	private final ManagedExecutorService executorService;

	public HTTPDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			ManagedExecutorService executorService) {
		super();
		this.jsonPathMetaModelServiceActivator = jsonPathMetaModelServiceActivator;
		this.groovyScriptServiceActivator = groovyScriptServiceActivator;
		this.executorService = executorService;
	}

	@Override
	public void startDC() {
		executorService.submit(() -> {
			try {
				jsonPathMetaModelServiceActivator.startAllServices();
				groovyScriptServiceActivator.startAllServices();
				logger.info("HTTP DC Initialization completed successfully");
			} catch (Exception e) {
				logger.error("Failed to start DC services");
				logExceptionStackTrace(e, getClass());
			}
		});
	}

	@Override
	public void stopDC() {
		try {
			jsonPathMetaModelServiceActivator.stopAllServices();
			groovyScriptServiceActivator.stopAllServices();
		} catch (Exception e) {
			logger.error("Failed to stop DC services ");
			logExceptionStackTrace(e, getClass());
		}
	}

}
