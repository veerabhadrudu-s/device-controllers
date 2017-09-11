/**
 * 
 */
package com.hpe.iot.northbound.service.outflow;

import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public interface DownlinkCommandServiceHandler {

	void processPayload(DeviceInfo deviceInfo);

}
