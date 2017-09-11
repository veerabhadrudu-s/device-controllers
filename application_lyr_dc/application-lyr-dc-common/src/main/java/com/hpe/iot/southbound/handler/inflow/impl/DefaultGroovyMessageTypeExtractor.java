/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceModel;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;

/**
 * @author sveera
 *
 */
public class DefaultGroovyMessageTypeExtractor implements MessageTypeExtractor {

	@Override
	public String extractMessageType(DeviceModel deviceModel, JsonObject payload) {
		return "default";
	}

}
