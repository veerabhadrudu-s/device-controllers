/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.model;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class GroovyScriptModel {

	private DeviceModel deviceModel;
	private final SouthboundGroovyScriptModel southboundGroovyScriptModel = new SouthboundGroovyScriptModel();
	private final NorthboundGroovyScriptModel northboundGroovyScriptModel = new NorthboundGroovyScriptModel();

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}

	public SouthboundGroovyScriptModel getSouthboundGroovyScriptModel() {
		return southboundGroovyScriptModel;
	}

	public NorthboundGroovyScriptModel getNorthboundGroovyScriptModel() {
		return northboundGroovyScriptModel;
	}

	@Override
	public String toString() {
		return "GroovyScriptModel [deviceModel=" + deviceModel + ", southboundGroovyScriptModel="
				+ southboundGroovyScriptModel + ", northboundGroovyScriptModel=" + northboundGroovyScriptModel + "]";
	}

}
