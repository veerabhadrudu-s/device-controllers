/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow;

import com.google.gson.JsonObject;
import com.hpe.iot.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface PayloadDecipher {

	JsonObject decipherPayload(DeviceModel deviceModel, byte[] payload);

}
