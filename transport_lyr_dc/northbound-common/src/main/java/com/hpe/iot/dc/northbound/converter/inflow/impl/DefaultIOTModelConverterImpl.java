/**
 * 
 */
package com.hpe.iot.dc.northbound.converter.inflow.impl;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class DefaultIOTModelConverterImpl extends AbstractIOTModelConverterImpl {

	private final DeviceModel deviceModel;

	public DefaultIOTModelConverterImpl(DeviceModel deviceModel) {
		super();
		this.deviceModel = deviceModel;
	}

	@Override
	public String getDeviceUniqueIDName() {
		return deviceModel.getManufacturer() + "_" + deviceModel.getModelId() + "_" + deviceModel.getVersion() + "_"
				+ "ID";
	}

}
