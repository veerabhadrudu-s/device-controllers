package com.hpe.iot.dc.northbound.converter.outflow;

import java.util.List;

/**
 * @author sveera
 *
 */
public interface ExtendedDownlinkDeviceDataConverter extends DownlinkDeviceDataConverter {

	List<String> getMessageTypes();
}
