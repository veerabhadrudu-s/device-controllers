package com.hpe.iot.dc.northbound.converter.outflow;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface DownlinkDeviceDataConverter {

	String getMessageType();

	DeviceInfo createModel(DeviceModel deviceModel, JsonObject downlinkData);

}
