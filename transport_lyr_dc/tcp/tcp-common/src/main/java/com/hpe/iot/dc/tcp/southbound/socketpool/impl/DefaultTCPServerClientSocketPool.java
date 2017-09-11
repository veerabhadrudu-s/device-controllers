package com.hpe.iot.dc.tcp.southbound.socketpool.impl;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public class DefaultTCPServerClientSocketPool implements ServerClientSocketPool {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Selector myClientSocketSelector;
	private final Map<Device, SocketChannel> deviceSockets = new ConcurrentHashMap<>();
	private final Map<SocketChannel, Device> socketToDevice = new ConcurrentHashMap<>();

	public DefaultTCPServerClientSocketPool() throws IOException {
		super();
		this.myClientSocketSelector = Selector.open();
	}

	@Override
	public Selector getClientSocketSelector() {
		return myClientSocketSelector;
	}

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
			myClientSocketSelector.close();
			for (Map.Entry<Device, SocketChannel> deviceSocket : deviceSockets.entrySet())
				closeDeviceSocket(deviceSocket.getKey(), deviceSocket.getValue());
			deviceSockets.clear();
		} catch (Exception e) {
			logger.error("Failed to close Server Socket resources ");
			UtilityLogger.logExceptionStackTrace(e, getClass());
		}

	}

	private void closePreviousSocket(Device device, SocketChannel newSocketChannel, SocketChannel previousSocketChannel)
			throws IOException {
		if (previousSocketChannel != null && !newSocketChannel.equals(previousSocketChannel)) {
			socketToDevice.remove(previousSocketChannel);
			closeDeviceSocket(device, previousSocketChannel);
		}
	}

	private void closeDeviceSocket(Device device, SocketChannel deviceSocket) throws IOException {
		logger.trace("Closing the socket " + deviceSocket.toString() + " for device " + device);
		deviceSocket.socket().close();
		deviceSocket.close();
	}

}
