/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceModel;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.jayway.jsonpath.JsonPath;

/**
 * @author sveera
 *
 */
public class JsonPathMessageTypeExtractor implements MessageTypeExtractor {

	@Override
	public String extractMessageType(DeviceModel deviceModel, JsonObject payload) {
		if (!(deviceModel instanceof JsonPathDeviceMetaModel))
			throw new RuntimeException(
					"Device Model is not instanceof " + JsonPathDeviceMetaModel.class.getSimpleName());
		JsonPathDeviceMetaModel jsonPathDeviceMetaModel = (JsonPathDeviceMetaModel) deviceModel;
		return jsonPathDeviceMetaModel.getMessageTypeJsonPath() != null
				&& !jsonPathDeviceMetaModel.getMessageTypeJsonPath().isEmpty()
						? extractJSONString(payload.toString(), jsonPathDeviceMetaModel.getMessageTypeJsonPath())
						: "default";
	}

	private String extractJSONString(String jsonString, String jsonExpession) {
		return JsonPath.parse(jsonString).read(jsonExpession);
	}

}
