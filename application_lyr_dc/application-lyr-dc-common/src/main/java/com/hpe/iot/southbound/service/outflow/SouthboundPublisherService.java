/**
 * 
 */
package com.hpe.iot.southbound.service.outflow;

import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface SouthboundPublisherService {

	void publishPayload(DeviceModel deviceModel, DeviceInfo decipheredPayload);

}
