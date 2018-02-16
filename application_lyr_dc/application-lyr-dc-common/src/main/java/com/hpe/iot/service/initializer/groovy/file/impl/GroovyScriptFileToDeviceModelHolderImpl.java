/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.file.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.service.initializer.groovy.file.GroovyScriptFileToDeviceModelHolder;

/**
 * @author sveera
 *
 */
public class GroovyScriptFileToDeviceModelHolderImpl implements GroovyScriptFileToDeviceModelHolder {

	private final Map<String, DeviceModel> groovyScriptFileDeviceModels = new ConcurrentHashMap<>();

	public void addScriptDeviceModel(String groovyScriptFile, DeviceModel deviceModel) {
		groovyScriptFileDeviceModels.put(groovyScriptFile, deviceModel);
	}

	public void removeScriptDeviceModel(String groovyScriptFile) {
		groovyScriptFileDeviceModels.remove(groovyScriptFile);
	}

	@Override
	public DeviceModel getScriptDeviceModel(String groovyScriptFile) {
		return groovyScriptFileDeviceModels.get(groovyScriptFile);
	}

	@Override
	public Map<String, DeviceModel> getScriptDeviceModel() {
		return new ConcurrentHashMap<>(groovyScriptFileDeviceModels);
	}

	@Override
	public String toString() {
		return "GroovyScriptFileToDeviceModelHolder [groovyScriptFileDeviceModels=" + groovyScriptFileDeviceModels
				+ "]";
	}

}
