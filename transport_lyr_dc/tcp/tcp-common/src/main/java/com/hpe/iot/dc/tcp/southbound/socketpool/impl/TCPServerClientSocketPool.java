package com.hpe.iot.dc.tcp.southbound.socketpool.impl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socket.listener.SocketChangeListener;

/**
 * @author sveera
 *
 */
public class TCPServerClientSocketPool extends DefaultTCPServerClientSocketPool {

	private final ServerSocketToDeviceModel serverSocketToDeviceModel;
	private final List<SocketChangeListener> socketChangeListeners;

	public TCPServerClientSocketPool(ServerSocketToDeviceModel serverSocketToDeviceModel,
			List<SocketChangeListener> socketChangeListeners) throws IOException {
		super();
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
		this.socketChangeListeners = socketChangeListeners;
	}

	@Override
	public void addSocketChannel(Device device, SocketChannel clientSocketChannel) throws IOException {
		if (!clientSocketChannel.isConnected())
			return;
		SocketChannel existingSocketChannel = deviceSockets.get(device);
		super.addSocketChannel(device, clientSocketChannel);
		if (existingSocketChannel == null)
			notifyChangedSocketCount();
	}

	@Override
	public void removeClientSocketChannel(SocketChannel socketChannel) {
		Device device = socketToDevice.get(socketChannel);
		super.removeClientSocketChannel(socketChannel);
		if (device != null)
			notifyChangedSocketCount();
	}

	@Override
	public void closeAllClientSockets() {
		super.closeAllClientSockets();
		notifyChangedSocketCount();
	}
	

	private void notifyChangedSocketCount() {
		for (SocketChangeListener socketChangeListener : socketChangeListeners) 
			socketChangeListener.handleChangedCount(serverSocketToDeviceModel, deviceSockets.keySet().size());
	}

}
