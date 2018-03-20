/**
 * 
 */
package com.hpe.iot.http.initializer;

import javax.enterprise.concurrent.ManagedExecutorService;

import com.hpe.iot.service.initializer.groovy.GroovyScriptMetaModelServiceActivator;
import com.hpe.iot.service.initializer.jsonpath.UplinkJsonPathMetaModelServiceActivator;

/**
 * @author sveera
 *
 */
public class SynchronousHTTPDCInitializer extends HTTPDCInitializer {

	public SynchronousHTTPDCInitializer(UplinkJsonPathMetaModelServiceActivator jsonPathMetaModelServiceActivator,
			GroovyScriptMetaModelServiceActivator groovyScriptServiceActivator,
			ManagedExecutorService executorService) {
		super(jsonPathMetaModelServiceActivator, groovyScriptServiceActivator, executorService);
	}

	@Override
	public void startDC() {
		startHTTPDC();
	}

}
