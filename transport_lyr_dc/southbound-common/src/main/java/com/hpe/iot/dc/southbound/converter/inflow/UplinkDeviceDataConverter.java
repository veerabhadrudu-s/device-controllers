package com.hpe.iot.dc.southbound.converter.inflow;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface UplinkDeviceDataConverter {

	String getMessageType();

	DeviceInfo createModel(DeviceModel deviceModel, byte[] input);

}
