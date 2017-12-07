/**
 * 
 */
package com.hpe.iot.dc.tcp.client.payload.converter;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;

/**
 * @author sveera
 *
 */
public interface ClientMessageGenerator {

	ClientDeviceData generateMessagePacket(long deviceId, String messageType);
}
