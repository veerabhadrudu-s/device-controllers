package com.hpe.iot.dc.southbound.converter.inflow;

import java.util.List;

/**
 * @author sveera
 *
 */
public interface ExtendedUplinkDeviceDataConverter extends UplinkDeviceDataConverter {

	List<String> getMessageTypes();
}
