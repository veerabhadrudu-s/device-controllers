/**
 * 
 */
package com.hpe.iot.southbound.service.inflow;

import com.google.gson.JsonObject;

/**
 * @author sveera
 *
 */
public interface SouthboundService {

	void processPayload(String manufacturer, String modelId, JsonObject payload);
}
