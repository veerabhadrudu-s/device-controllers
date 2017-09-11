/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.hpe.iot.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;

/**
 * @author sveera
 *
 */
public class DefaultUplinkPayloadProcessor implements UplinkPayloadProcessor {

	private final IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService;

	public DefaultUplinkPayloadProcessor(
			IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService) {
		super();
		this.iotPublisherService = iotPublisherService;
	}

	@Override
	public void processPayload(DeviceInfo decipheredPayload) {
		iotPublisherService.receiveDataFromDevice(decipheredPayload, decipheredPayload.getMessageType());
	}

}
