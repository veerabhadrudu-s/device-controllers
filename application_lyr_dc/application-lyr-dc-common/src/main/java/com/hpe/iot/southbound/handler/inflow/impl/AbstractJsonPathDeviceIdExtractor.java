/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.jayway.jsonpath.JsonPath;

/**
 * @author sveera
 *
 */
public abstract class AbstractJsonPathDeviceIdExtractor implements DeviceIdExtractor {

	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload) {
		return extractJSONString(payload.toString(), getDeviceIdJsonPath());
	}

	public abstract String getDeviceIdJsonPath();	

	private String extractJSONString(String jsonString, String jsonExpession) {
		return JsonPath.parse(jsonString).read(jsonExpession);
	}

}
