/**
 * 
 */
package com.hpe.iot.model.impl;

import com.hpe.iot.dc.model.DeviceModelImpl;

/**
 * @author sveera
 *
 */
public class GroovyScriptDeviceModel extends DeviceModelImpl {

	public GroovyScriptDeviceModel(String manufacturer, String modelId, String version) {
		super(manufacturer, modelId, version);
	}

	@Override
	public String toString() {
		return "GroovyScriptDeviceModel [getManufacturer()=" + getManufacturer() + ", getModelId()=" + getModelId()
				+ ", getVersion()=" + getVersion() + "]";
	}

}
