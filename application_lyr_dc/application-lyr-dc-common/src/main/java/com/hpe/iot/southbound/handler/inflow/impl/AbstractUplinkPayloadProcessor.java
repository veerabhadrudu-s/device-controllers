/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;

/**
 * @author sveera
 *
 */
public abstract class AbstractUplinkPayloadProcessor implements UplinkPayloadProcessor {

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;

	public AbstractUplinkPayloadProcessor(
			IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService) {
		super();
		this.iotPublisherService = iotPublisherService;
	}

}
