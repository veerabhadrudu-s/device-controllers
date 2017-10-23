/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;

/**
 * @author sveera
 *
 */
public class DefaultPayloadDecipher implements PayloadDecipher {
	
	private final JsonParser jsonParser = new JsonParser();

	@Override
	public JsonObject decipherPayload(DeviceModel deviceModel, byte[] payload) {
		return (JsonObject) jsonParser.parse(new String(payload));
	}

}
