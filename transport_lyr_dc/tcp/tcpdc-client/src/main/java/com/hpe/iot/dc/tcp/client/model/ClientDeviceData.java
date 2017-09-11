/**
 * 
 */
package com.hpe.iot.dc.tcp.client.model;

import java.util.Arrays;

/**
 * @author sveera
 *
 */
public class ClientDeviceData {

	private final long deviceId;
	private final byte[] messageData;
	private final String messagetype;

	public ClientDeviceData(long deviceId, byte[] messageData, String messagetype) {
		super();
		this.deviceId = deviceId;
		this.messageData = messageData;
		this.messagetype = messagetype;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public byte[] getMessageData() {
		return messageData;
	}

	public String getMessagetype() {
		return messagetype;
	}

	@Override
	public String toString() {
		return "ClientDeviceData [deviceId=" + deviceId + ", messageData=" + Arrays.toString(messageData)
				+ ", messagetype=" + messagetype + "]";
	}

}
