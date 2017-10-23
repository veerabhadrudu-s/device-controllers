/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.jayway.jsonpath.JsonPath;

/**
 * @author sveera
 *
 */
public class JsonPathDeviceIdExtractor implements DeviceIdExtractor {

	@Override
	public String extractDeviceId(DeviceModel deviceModel, JsonObject payload) {
		if (!(deviceModel instanceof JsonPathDeviceMetaModel))
			throw new RuntimeException(
					"Device Model is not instanceof " + JsonPathDeviceMetaModel.class.getSimpleName());
		JsonPathDeviceMetaModel jsonPathDeviceMetaModel = (JsonPathDeviceMetaModel) deviceModel;
		return extractJSONString(payload.toString(), jsonPathDeviceMetaModel.getDeviceIdJsonPath());
	}

	private String extractJSONString(String jsonString, String jsonExpession) {
		return JsonPath.parse(jsonString).read(jsonExpession);
	}

}
