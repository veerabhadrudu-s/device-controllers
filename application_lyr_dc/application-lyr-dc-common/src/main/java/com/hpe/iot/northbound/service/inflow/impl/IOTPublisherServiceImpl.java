package com.hpe.iot.northbound.service.inflow.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;
import com.hpe.iot.model.DeviceDataDeliveryStatus;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.converter.inflow.IOTModelConverter;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;

/**
 * @author sveera
 *
 */
public class IOTPublisherServiceImpl implements IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final IOTModelConverter iotModelConverter;
	private final IOTPublisherHandler iotPublisherHandler;

	public IOTPublisherServiceImpl(IOTPublisherHandler iotPublisherHandler, IOTModelConverter iotModelConverter) {
		super();
		this.iotPublisherHandler = iotPublisherHandler;
		this.iotModelConverter = iotModelConverter;
	}

	@Override
	public DeviceDataDeliveryStatus receiveDataFromDevice(DeviceInfo request, String containerName) {
		logger.trace("Received Device data  " + request + " for container " + containerName);
		RequestPrimitive requestPrimitive = iotModelConverter.convertToRequestPrimitive(request, containerName);
		logger.debug("Posting request primitive to DAV " + requestPrimitive);
		ResponsePrimitive responsePrimitive=iotPublisherHandler.sendDataToIot(requestPrimitive);
		return iotModelConverter.convertToDeviceDataDeliveryStatus(responsePrimitive);
	}
}
