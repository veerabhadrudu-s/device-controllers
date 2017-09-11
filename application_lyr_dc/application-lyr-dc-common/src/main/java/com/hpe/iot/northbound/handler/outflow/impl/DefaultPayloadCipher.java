/**
 * 
 */
package com.hpe.iot.northbound.handler.outflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceModel;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;

/**
 * @author sveera
 *
 */
public class DefaultPayloadCipher implements PayloadCipher {

	@Override
	public JsonObject cipherPayload(DeviceModel deviceModel, JsonObject payload) {
		return payload;
	}

}
