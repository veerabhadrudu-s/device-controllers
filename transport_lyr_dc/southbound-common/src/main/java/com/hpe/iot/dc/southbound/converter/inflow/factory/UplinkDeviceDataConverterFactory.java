package com.hpe.iot.dc.southbound.converter.inflow.factory;

import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;

/**
 * @author sveera
 *
 */
public interface UplinkDeviceDataConverterFactory {

	UplinkDeviceDataConverter getModelConverter(String messageType);
}
