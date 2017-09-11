package com.hpe.iot.dc.northbound.converter.inflow;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;

/**
 * @author sveera
 *
 */
public interface IOTModelConverter {

	RequestPrimitive convertToRequestPrimitive(DeviceInfo deviceData, String containerName);

	DeviceDataDeliveryStatus convertToDeviceDataDeliveryStatus(ResponsePrimitive responsePrimitive);

}
