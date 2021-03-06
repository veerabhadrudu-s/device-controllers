/**
 * 
 */
package com.hpe.iot.model.factory.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.model.factory.GroovyDeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;

/**
 * @author sveera
 *
 */
public class GroovyAndUplinkJsonPathDeviceModelFactory implements GroovyDeviceModelFactory {

	private final Map<DeviceModel, GroovyScriptDeviceModel> groovyDeviceMetaModels = new ConcurrentHashMap<DeviceModel, GroovyScriptDeviceModel>();
	private final UplinkJsonPathDeviceModelFactory uplinkJsonPathDeviceModelFactory;

	public GroovyAndUplinkJsonPathDeviceModelFactory(String jsonPathMetaModelConfigurationPath) {
		uplinkJsonPathDeviceModelFactory = new UplinkJsonPathDeviceModelFactory(jsonPathMetaModelConfigurationPath);
	}

	@Override
	public void addGroovyDeviceModel(String manufacturer, String modelId, String version,
			GroovyScriptDeviceModel groovyScriptDeviceMetaModel) {
		DeviceModel deviceModel = new DeviceModelImpl(manufacturer, modelId, version);
		groovyDeviceMetaModels.put(deviceModel, groovyScriptDeviceMetaModel);
	}

	public void removeGroovyDeviceModel(String manufacturer, String modelId, String version) {
		DeviceModel deviceModel = new DeviceModelImpl(manufacturer, modelId, version);
		groovyDeviceMetaModels.remove(deviceModel);
	}

	@Override
	public DeviceModel findDeviceModel(String manufacturer, String modelId, String version) {
		DeviceModel deviceModel = new DeviceModelImpl(manufacturer, modelId, version);
		return groovyDeviceMetaModels.get(deviceModel) == null
				? uplinkJsonPathDeviceModelFactory.findDeviceModel(manufacturer, modelId, version)
				: groovyDeviceMetaModels.get(deviceModel);
	}

	@Override
	public List<DeviceModel> getAllDeviceModels() {
		List<DeviceModel> deviceModels = uplinkJsonPathDeviceModelFactory.getAllDeviceModels();
		deviceModels.addAll(groovyDeviceMetaModels.values());
		return deviceModels;
	}

	@Override
	public String toString() {
		return "GroovyAndJsonPathCombinedDeviceMetaModelFactory [groovyDeviceMetaModels=" + groovyDeviceMetaModels
				+ ", uplinkJsonPathDeviceModelFactory=" + uplinkJsonPathDeviceModelFactory + "]";
	}

}
