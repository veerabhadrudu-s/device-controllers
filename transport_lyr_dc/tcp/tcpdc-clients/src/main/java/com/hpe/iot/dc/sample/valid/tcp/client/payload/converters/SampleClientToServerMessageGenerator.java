/**
 * 
 */
package com.hpe.iot.dc.sample.valid.tcp.client.payload.converters;

import java.util.Date;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class SampleClientToServerMessageGenerator implements ClientMessageGenerator {

	@Override
	public ClientDeviceData generateMessagePacket(long deviceId, String messageType) {
		return new ClientDeviceData(deviceId, ("<" + String.format("%010d", deviceId) + "#"
				+ "Message Generated Time is : " + new Date().toString() + ">").getBytes(), messageType);
	}
}
