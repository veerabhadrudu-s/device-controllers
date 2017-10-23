/**
 * 
 */
package com.hpe.iot.model.factory;

import com.hpe.iot.model.impl.GroovyScriptDeviceModel;

/**
 * @author sveera
 *
 */
public interface GroovyDeviceModelFactory extends DeviceModelFactory {

	void addGroovyDeviceModel(String manufacturer, String modelId, String version,
			GroovyScriptDeviceModel groovyScriptDeviceMetaModel);

}
