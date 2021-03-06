package com.hpe.iot.dc.tcp.southbound.socketpool.impl;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class DefaultTCPServerClientSocketPool implements ServerClientSocketPool {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected final Map<Device, SocketChannel> deviceSockets = new ConcurrentHashMap<>();
	protected final Map<SocketChannel, Device> socketToDevice = new ConcurrentHashMap<>();

	@Override
	public List<Device> getDevices() {
		return new CopyOnWriteArrayList<>(deviceSockets.keySet());
	}

	@Override
	public SocketChannel getClientSocket(Device device) {
		return deviceSockets.get(device);
	}

	@Override
	public Device getDevice(SocketChannel socketChannel) {
		return socketToDevice.get(socketChannel);
	}

	@Override
	public void addSocketChannel(Device device, SocketChannel clientSocketChannel) throws IOException {
		if (!clientSocketChannel.isConnected())
			return;
		logger.trace("Adding client socket channnel to socket pool " + clientSocketChannel + " for Device " + device);
		SocketChannel existingSocketChannel = deviceSockets.put(device, clientSocketChannel);
		closePreviousSocket(device, clientSocketChannel, existingSocketChannel);
		socketToDevice.put(clientSocketChannel, device);
	}

	@Override
	public void removeClientSocketChannel(SocketChannel socketChannel) {
		Device device = socketToDevice.get(socketChannel);
		if (device != null) {
			deviceSockets.remove(device);
			logger.trace(
					"Removing client socket channnel from socket Pool " + socketChannel + " for Device ID " + device);
		}
		socketToDevice.remove(socketChannel);
	}

	@Override
	public void closeAllClientSockets() {
		try {
			logger.info("Closing all active sockets " + deviceSockets.size());
			for (Map.Entry<Device, SocketChannel> deviceSocket : deviceSockets.entrySet())
				tryClosingSocket(deviceSocket.getKey(), deviceSocket.getValue());
		} finally {
			deviceSockets.clear();
			socketToDevice.clear();
		}

	}

	private void closePreviousSocket(Device device, SocketChannel newSocketChannel, SocketChannel previousSocketChannel)
			throws IOException {
		if (previousSocketChannel != null && !newSocketChannel.equals(previousSocketChannel)) {
			socketToDevice.remove(previousSocketChannel);
			closeDeviceSocket(device, previousSocketChannel);
		}
	}

	private void tryClosingSocket(Device key, SocketChannel value) {
		try {
			closeDeviceSocket(key, value);
		} catch (Exception e) {
			logExceptionStackTrace(e, getClass());
		}
	}

	private void closeDeviceSocket(Device device, SocketChannel deviceSocket) throws IOException {
		logger.trace("Closing the socket " + deviceSocket.toString() + " for device " + device);
		deviceSocket.socket().close();
		deviceSocket.close();
	}

}
