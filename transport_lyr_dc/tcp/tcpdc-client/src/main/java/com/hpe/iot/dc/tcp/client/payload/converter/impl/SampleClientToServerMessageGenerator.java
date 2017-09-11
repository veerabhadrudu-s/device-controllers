/**
 * 
 */
package com.hpe.iot.dc.tcp.client.payload.converter.impl;

import java.util.Date;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientToServerMessageGenerator;

/**
 * @author sveera
 *
 */
public class SampleClientToServerMessageGenerator implements ClientToServerMessageGenerator {

	@Override
	public ClientDeviceData generateMessagePacket(long deviceId, String messageType) {
		return new ClientDeviceData(deviceId, ("<" + String.format("%010d", deviceId) + "#"
				+ "Message Generated Time is : " + new Date().toString() + ">").getBytes(), messageType);
	}
}
