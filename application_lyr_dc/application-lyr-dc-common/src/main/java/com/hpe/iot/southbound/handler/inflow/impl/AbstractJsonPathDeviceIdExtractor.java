/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.jayway.jsonpath.JsonPath;

/**
 * @author sveera
 *
 */
public abstract class AbstractJsonPathDeviceIdExtractor implements DeviceIdExtractor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload) {
		logger.trace("Device JsonPayload received for deviceId extraction is " + payload.toString());
		return extractJSONString(payload.toString(), getDeviceIdJsonPath());
	}

	public abstract String getDeviceIdJsonPath();

	private String extractJSONString(String jsonString, String jsonExpession) {
		return JsonPath.parse(jsonString).read(jsonExpession);
	}

}
