/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface MessageTypeExtractor {

	String extractMessageType(DeviceModel deviceModel, JsonObject payload);

}
