/**
 * 
 */
package com.hpe.iot.model.impl;

import com.hpe.iot.model.DeviceModelImpl;

/**
 * @author sveera
 *
 */
public class GroovyScriptDeviceModel extends DeviceModelImpl {

	public GroovyScriptDeviceModel(String manufacturer, String modelId) {
		super(manufacturer, modelId);
	}

	@Override
	public String toString() {
		return "GroovyScriptDeviceModel [getManufacturer()=" + getManufacturer() + ", getModelId()=" + getModelId()
				+ "]";
	}

}
