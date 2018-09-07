/**
 * 
 */
package com.hpe.iot.dc.tcp.web.plugin.monitor;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.model.AbstractServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.factory.SocketPoolFactoryReader;

/**
 * @author sveera
 *
 */
public class SocketPoolFactoryReaderStub implements SocketPoolFactoryReader {

	private Map<ServerSocketToDeviceModel, ServerClientSocketPool> deviceModelToSocketPool = new ConcurrentHashMap<>();

	public SocketPoolFactoryReaderStub() {
		super();
		this.deviceModelToSocketPool.put(new ServerSocketToDeviceModelStub(), new TCPServerClientSocketPoolStub());
	}

	@Override
	public Map<ServerSocketToDeviceModel, ServerClientSocketPool> getDeviceModelToSocketPool() {
		return deviceModelToSocketPool;
	}

	class ServerSocketToDeviceModelStub extends AbstractServerSocketToDeviceModel {

		@Override
		public String getBoundLocalAddress() {
			return null;
		}

		@Override
		public int getPortNumber() {
			return 0;
		}

		@Override
		public String getManufacturer() {
			return "MMI";
		}

		@Override
		public String getModelId() {
			return "Safemate";
		}

		@Override
		public String getVersion() {
			return "1.0";
		}
	}

	private class TCPServerClientSocketPoolStub implements ServerClientSocketPool {

		@Override
		public Device getDevice(SocketChannel socketChannel) {
			return null;
		}

		@Override
		public List<Device> getDevices() {
			return Collections.emptyList();
		}

		@Override
		public SocketChannel getClientSocket(Device device) {
			return null;
		}

		@Override
		public void addSocketChannel(Device device, SocketChannel clientSocketChannel) throws IOException {

		}

		@Override
		public void removeClientSocketChannel(SocketChannel socketChannel) {

		}

		@Override
		public void closeAllClientSockets() {

		}

	}

}
