/**
 * 
 */
package com.hpe.iot.dc.southbound.converter.inflow.session;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;

/**
 * @author sveera
 *
 */
public abstract class SessionBasedUplinkDeviceDataConverter implements UplinkDeviceDataConverter {

	@Override
	public DeviceInfo createModel(DeviceModel deviceModel, byte[] input) {
		if (deviceModel == null || !(deviceModel instanceof Device))
			return null;
		return createModel((Device) deviceModel, input);
	}

	protected abstract DeviceInfo createModel(Device device, byte[] input);

}
