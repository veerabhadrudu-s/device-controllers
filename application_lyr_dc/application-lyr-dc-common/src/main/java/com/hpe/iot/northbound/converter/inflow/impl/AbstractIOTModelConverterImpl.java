package com.hpe.iot.northbound.converter.inflow.impl;

import java.math.BigInteger;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.m2m.common.ContentInstance;
import com.hpe.iot.m2m.common.PrimitiveContent;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;
import com.hpe.iot.m2m.common.ResponseTypeInfo;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.converter.inflow.IOTModelConverter;

/**
 * @author sveera
 *
 */
public abstract class AbstractIOTModelConverterImpl implements IOTModelConverter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public RequestPrimitive convertToRequestPrimitive(DeviceInfo deviceData, String containerName) {
		logger.trace("Started Converting Device Data to Request Primitive ");
		RequestPrimitive requestPrimitive = new RequestPrimitive();
		requestPrimitive.setOperation(BigInteger.valueOf(1L));
		requestPrimitive.setResourceType(BigInteger.valueOf(4L));
		String toField = containerName != null
				? getDeviceUniqueIDName(deviceData) + ":" + deviceData.getDevice().getDeviceId() + "/" + containerName
				: getDeviceUniqueIDName(deviceData) + ":" + deviceData.getDevice().getDeviceId();
		requestPrimitive.setTo(toField);
		requestPrimitive.setRequestIdentifier(generateUniqueIdentifierBasedOnTimeStamp(deviceData));
		ResponseTypeInfo resp = new ResponseTypeInfo();
		resp.getNotificationURI().add(null);
		resp.setResponseTypeValue(BigInteger.valueOf(2L));
		requestPrimitive.setResponseType(resp);
		requestPrimitive.setFrom(getDeviceUniqueIDName(deviceData) + ":" + deviceData.getDevice().getDeviceId());
		PrimitiveContent content = new PrimitiveContent();
		ContentInstance ci = new ContentInstance();
		ci.setContentInfo("text/plain:0");
		ci.setContentSize(BigInteger.valueOf(50L));
		logger.trace("Content Instance Data is " + new Gson().toJson(deviceData));
		ci.setContent(new Gson().toJson(deviceData));
		content.getAny().add(ci);
		requestPrimitive.setPrimitiveContent(content);
		logger.trace("Completed Converting Device Data to Request Primitive ");
		return requestPrimitive;
	}

	private String generateUniqueIdentifierBasedOnTimeStamp(DeviceInfo deviceData) {
		return getDeviceUniqueIDName(deviceData) + new Date().getTime();
	}

	@Override
	public DeviceDataDeliveryStatus convertToDeviceDataDeliveryStatus(ResponsePrimitive responsePrimitive) {
		return new DeviceDataDeliveryStatus();
	}

	public abstract String getDeviceUniqueIDName(DeviceInfo deviceData);
}
