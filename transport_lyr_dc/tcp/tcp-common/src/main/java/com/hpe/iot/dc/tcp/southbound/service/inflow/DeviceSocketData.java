/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow;

import java.nio.channels.SocketChannel;

/**
 * @author sveera
 *
 */
public class DeviceSocketData {

	private final byte[] dataFromClientSocket;
	private final SocketChannel clientSocket;

	public DeviceSocketData(byte[] dataFromClientSocket, SocketChannel clientSocket) {
		super();
		this.dataFromClientSocket = dataFromClientSocket;
		this.clientSocket = clientSocket;
	}

	public byte[] getDataFromClientSocket() {
		return dataFromClientSocket;
	}

	public SocketChannel getClientSocket() {
		return clientSocket;
	}
}
