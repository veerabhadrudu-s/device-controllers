/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.file;

import java.util.Map;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface GroovyScriptFileToDeviceModelHolder {

	DeviceModel getScriptDeviceModel(String groovyScriptFile);

	Map<String, DeviceModel> getScriptDeviceModel();
}
