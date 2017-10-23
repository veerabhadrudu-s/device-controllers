/**
 * 
 */
package com.hpe.iot.southbound.service.outflow;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public interface SouthboundPublisherService {

	void publishPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload);

	void publishPayload(DeviceModel deviceModel, String deviceId, byte[] decipheredPayload);

}
