/**
 * 
 */
package com.hpe.iot.kafka.southbound.service.inflow;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface DeviceModelkafkaSubscriptionService {

	void subscribeForDeviceModel(final DeviceModel deviceModel);

	void unsubscribeForDeviceModel(final DeviceModel deviceModel);
}
