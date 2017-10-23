package com.hpe.iot.northbound.converter.inflow;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public interface IOTModelConverter {

	RequestPrimitive convertToRequestPrimitive(DeviceInfo deviceData, String containerName);

	DeviceDataDeliveryStatus convertToDeviceDataDeliveryStatus(ResponsePrimitive responsePrimitive);

}
