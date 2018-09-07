/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool.factory.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socket.listener.SocketChangeListener;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.factory.TCPServerClientSocketPoolFactory;
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.TCPServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class TCPServerClientSocketPoolFactoryImpl implements TCPServerClientSocketPoolFactory {

	private final List<SocketChangeListener> socketChangeListeners;
	private final Map<ServerSocketToDeviceModel, ServerClientSocketPool> deviceModelToSocketPool;

	public TCPServerClientSocketPoolFactoryImpl(List<SocketChangeListener> socketChangeListeners) {
		super();
		this.socketChangeListeners = socketChangeListeners;
		this.deviceModelToSocketPool = new ConcurrentHashMap<>();
	}

	@Override
	public ServerClientSocketPool getServerClientSocketPool(ServerSocketToDeviceModel serverSocketToDeviceModel)
			throws IOException {
		if (deviceModelToSocketPool.get(serverSocketToDeviceModel) == null)
			deviceModelToSocketPool.put(serverSocketToDeviceModel,
					new TCPServerClientSocketPool(serverSocketToDeviceModel, socketChangeListeners));
		return deviceModelToSocketPool.get(serverSocketToDeviceModel);
	}

	@Override
	public boolean removeServerClientSocketPool(ServerSocketToDeviceModel serverSocketToDeviceModel) {
		return deviceModelToSocketPool.remove(serverSocketToDeviceModel) != null;
	}

	@Override
	public Map<ServerSocketToDeviceModel, ServerClientSocketPool> getDeviceModelToSocketPool() {
		return new ConcurrentHashMap<>(deviceModelToSocketPool);
	}

}
