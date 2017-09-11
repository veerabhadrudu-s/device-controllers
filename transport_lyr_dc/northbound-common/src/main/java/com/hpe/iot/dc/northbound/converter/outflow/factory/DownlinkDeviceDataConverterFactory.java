package com.hpe.iot.dc.northbound.converter.outflow.factory;

import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;

/**
 * @author sveera
 *
 */
public interface DownlinkDeviceDataConverterFactory {

	DownlinkDeviceDataConverter getModelConverter(String messageType);
}
