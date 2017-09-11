package com.hpe.iot.dc.northbound.converter.inflow.impl;

import java.math.BigInteger;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.northbound.converter.inflow.IOTModelConverter;
import com.hpe.iot.m2m.common.ContentInstance;
import com.hpe.iot.m2m.common.PrimitiveContent;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;
import com.hpe.iot.m2m.common.ResponseTypeInfo;

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
				? getDeviceUniqueIDName() + ":" + deviceData.getDevice().getDeviceId() + "/" + containerName
				: getDeviceUniqueIDName() + ":" + deviceData.getDevice().getDeviceId();
		requestPrimitive.setTo(toField);
		requestPrimitive.setRequestIdentifier(generateUniqueIdentifierBasedOnTimeStamp());
		ResponseTypeInfo resp = new ResponseTypeInfo();
		resp.getNotificationURI().add(null);
		resp.setResponseTypeValue(BigInteger.valueOf(2L));
		requestPrimitive.setResponseType(resp);
		requestPrimitive.setFrom(getDeviceUniqueIDName() + ":" + deviceData.getDevice().getDeviceId());
		PrimitiveContent content = new PrimitiveContent();
		ContentInstance ci = new ContentInstance();
		ci.setContentInfo("text/plain:0");
		ci.setContentSize(BigInteger.valueOf(50L));
		logger.trace("Content Instance Data is " + new Gson().toJson(deviceData));
		ci.setContent(new Gson().toJson(deviceData));
		content.getAny().add(ci);
		requestPrimitive.setPrimitiveContent(content);
		return requestPrimitive;
	}

	private String generateUniqueIdentifierBasedOnTimeStamp() {
		return getDeviceUniqueIDName() + new Date().getTime();
	}

	@Override
	public DeviceDataDeliveryStatus convertToDeviceDataDeliveryStatus(ResponsePrimitive responsePrimitive) {
		return new DeviceDataDeliveryStatus();
	}

	public abstract String getDeviceUniqueIDName();
}
