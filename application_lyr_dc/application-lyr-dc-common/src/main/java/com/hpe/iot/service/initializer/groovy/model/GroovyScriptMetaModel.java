/**
 * 
 */
package com.hpe.iot.service.initializer.groovy.model;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class GroovyScriptMetaModel {

	private Class<? extends DeviceModel> deviceModelClasstype;
	private final SouthboundGroovyScriptMetaModel southboundGroovyScriptMetaModel = new SouthboundGroovyScriptMetaModel();
	private final NorthboundGroovyScriptMetaModel northboundGroovyScriptMetaModel = new NorthboundGroovyScriptMetaModel();

	public Class<? extends DeviceModel> getDeviceModelClasstype() {
		return deviceModelClasstype;
	}

	public void setDeviceModelClasstype(Class<? extends DeviceModel> deviceModelClasstype) {
		this.deviceModelClasstype = deviceModelClasstype;
	}

	public SouthboundGroovyScriptMetaModel getSouthboundGroovyScriptMetaModel() {
		return southboundGroovyScriptMetaModel;
	}

	public NorthboundGroovyScriptMetaModel getNorthboundGroovyScriptMetaModel() {
		return northboundGroovyScriptMetaModel;
	}

	@Override
	public String toString() {
		return "GroovyScriptMetaModel [deviceModelClasstype=" + deviceModelClasstype
				+ ", southboundGroovyScriptMetaModel=" + southboundGroovyScriptMetaModel
				+ ", northboundGroovyScriptMetaModel=" + northboundGroovyScriptMetaModel + "]";
	}

}
