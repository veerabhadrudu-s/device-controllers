/**
 * 
 */
package com.hpe.iot.northbound.converter.inflow.impl;

import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class DefaultIOTModelConverterImpl extends AbstractIOTModelConverterImpl {

	@Override
	public String getDeviceUniqueIDName(DeviceInfo deviceData) {
		return deviceData.getDevice().getManufacturer() + "_" + deviceData.getDevice().getModelId() + "_"
				+ deviceData.getDevice().getVersion() + "_" + "ID";
	}

}
