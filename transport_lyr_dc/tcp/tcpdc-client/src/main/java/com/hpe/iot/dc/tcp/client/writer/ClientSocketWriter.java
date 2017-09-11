/**
 * 
 */
package com.hpe.iot.dc.tcp.client.writer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientToServerMessageGenerator;

/**
 * @author sveera
 *
 */
public class ClientSocketWriter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClientToServerMessageGenerator clientToServerMessageGenerator;
	private final int index;

	public ClientSocketWriter(int index, ClientToServerMessageGenerator clientToServerMessageGenerator) {
		super();
		this.clientToServerMessageGenerator = clientToServerMessageGenerator;
		this.index = index;
	}

	public void pushDataUsingClients(String messageType, DeviceSocketModel... deviceClients)
			throws IOException, InterruptedException {
		for (DeviceSocketModel deviceClient : deviceClients) {
			ClientDeviceData clientDeviceData = clientToServerMessageGenerator
					.generateMessagePacket(deviceClient.getDeviceId(), messageType);
			if (clientDeviceData == null)
				continue;
			tryWritingOnConnectedClient(deviceClient, clientDeviceData);

		}
	}

	private void tryWritingOnConnectedClient(DeviceSocketModel deviceClient, ClientDeviceData clientDeviceData)
			throws IOException {
		if (deviceClient.getSocketChannel().isConnected()) {
			postMessage(clientDeviceData.getMessageData(), deviceClient.getSocketChannel());
			logger.debug(toString() + "Completed writing on client socket port "
					+ deviceClient.getSocketChannel().socket().getLocalPort() + " with Device ID "
					+ deviceClient.getDeviceId());
		} else if (deviceClient.getSocketChannel().isConnectionPending()) {
			logger.info(toString() + "Waiting for the socket connection"
					+ deviceClient.getSocketChannel().socket().getLocalPort() + " with Device ID "
					+ deviceClient.getDeviceId());
			deviceClient.getSocketChannel().finishConnect();

		} else if (!deviceClient.getSocketChannel().isConnected()) {
			logger.warn(
					toString() + "Socket was already closed " + deviceClient.getSocketChannel().socket().getLocalPort()
							+ " with Device ID " + deviceClient.getDeviceId());
		}
	}

	private void postMessage(byte[] message, SocketChannel socketChannel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(message.length);
		byteBuffer.put(message);
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
		byteBuffer.clear();
	}

	@Override
	public String toString() {
		return "ClientSocketWriter [index=" + index + "]";
	}

}
