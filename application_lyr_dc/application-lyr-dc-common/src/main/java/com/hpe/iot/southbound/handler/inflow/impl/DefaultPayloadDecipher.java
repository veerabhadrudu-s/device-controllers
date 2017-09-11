/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceModel;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;

/**
 * @author sveera
 *
 */
public class DefaultPayloadDecipher implements PayloadDecipher {

	@Override
	public JsonObject decipherPayload(DeviceModel deviceModel, JsonObject payload) {
		return payload;
	}

}
