/**
 * 
 */
package com.hpe.iot.dc.tcp.client.payload.converter;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;

/**
 * @author sveera
 *
 */
public interface ServerToClientMessageGenerator {

	ClientDeviceData handleMessage(byte[] message);

}
