/**
 * 
 */
package com.hpe.iot.southbound.service.inflow;

/**
 * @author sveera
 *
 */
public interface SouthboundService {

	void processPayload(String manufacturer, String modelId, byte[] payload);
}
