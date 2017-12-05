/**
 * 
 */
package com.hpe.iot.dc.tcp.client.model;

import java.nio.channels.SocketChannel;

/**
 * @author sveera
 *
 */
public class DeviceSocketModel {

	private final long deviceId;
	private final SocketChannel socketChannel;

	public DeviceSocketModel(long deviceId, SocketChannel socketChannel) {
		super();
		this.deviceId = deviceId;
		this.socketChannel = socketChannel;
	}

	public long getDeviceId() {
		return deviceId;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	@Override
	public String toString() {
		return "DeviceSocketModel [deviceId=" + deviceId + ", socketChannel=" + socketChannel + "]";
	}
}
