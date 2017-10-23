/**
 * 
 */
package com.hpe.iot.northbound.handler.outflow;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface PayloadCipher {
	
	JsonObject cipherPayload(DeviceModel deviceModel, JsonObject payload);

}
