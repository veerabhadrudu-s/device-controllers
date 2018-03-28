/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModelDummy;
import com.hpe.iot.dc.tcp.southbound.socket.listener.SocketChangeListener;

/**
 * @author sveera
 *
 */
public class TCPServerClientSocketPoolTest {

	private final SocketChangeListenerSpy socketChangeListenerSpy = new SocketChangeListenerSpy();
	private TCPServerClientSocketPool tcpServerClientSocketPool;

	@BeforeEach
	public void setUp() throws Exception {
		List<SocketChangeListener> socketChangeListeners = new ArrayList<>();
		socketChangeListeners.add(socketChangeListenerSpy);
		tcpServerClientSocketPool = new TCPServerClientSocketPool(new ServerSocketToDeviceModelDummy(),
				socketChangeListeners);
	}

	@CsvSource({ "1", "2", "10" })
	@DisplayName("test Add Socket Channel with ")
	@ParameterizedTest(name = " {0} sockets channel and expect {0} device count change notifications")
	public void testAddSocketChannel(int noOfSocketsToBeTested) throws IOException {
		List<SocketChannelStub> addedSocketList = new ArrayList<>();
		Integer[] expectedDeviceCountHistoryArray = new Integer[noOfSocketsToBeTested];
		for (int i = 1; i <= noOfSocketsToBeTested; i++) {
			expectedDeviceCountHistoryArray[i - 1] = i;
			Device device = new DeviceImpl("manufacturer", "modelId", "version", Integer.toString(i));
			SocketChannelStub socketChannel = new SocketChannelStub(device);
			addedSocketList.add(socketChannel);
			tcpServerClientSocketPool.addSocketChannel(device, socketChannel);
		}
		for (SocketChannelStub socketChannel : addedSocketList)
			assertEquals(socketChannel.device, tcpServerClientSocketPool.getDevice(socketChannel));
		assertEquals(noOfSocketsToBeTested, tcpServerClientSocketPool.getDevices().size());
		assertThat(socketChangeListenerSpy.deviceCountChangeHistory, contains(expectedDeviceCountHistoryArray));
	}

	@CsvSource({ "1", "2", "10" })
	@DisplayName("test Update Socket Channel with ")
	@ParameterizedTest(name = " {0} sockets channel and expect {0} device count change notifications")
	public void testUpdateSocketChannel(int noOfSocketsToBeTested) throws IOException {
		List<SocketChannelStub> addedSocketList = new ArrayList<>();
		Integer[] expectedDeviceCountHistoryArray = new Integer[noOfSocketsToBeTested];
		for (int i = 1; i <= noOfSocketsToBeTested; i++) {
			expectedDeviceCountHistoryArray[i - 1] = i;
			Device device = new DeviceImpl("manufacturer", "modelId", "version", Integer.toString(i));
			SocketChannelStub socketChannel1 = new SocketChannelStub(device);
			SocketChannelStub socketChannel2 = new SocketChannelStub(device);
			addedSocketList.add(socketChannel2);
			tcpServerClientSocketPool.addSocketChannel(device, socketChannel1);
			tcpServerClientSocketPool.addSocketChannel(device, socketChannel2);
		}
		for (SocketChannelStub socketChannel : addedSocketList)
			assertEquals(socketChannel.device, tcpServerClientSocketPool.getDevice(socketChannel));
		assertEquals(noOfSocketsToBeTested, tcpServerClientSocketPool.getDevices().size());
		assertThat(socketChangeListenerSpy.deviceCountChangeHistory, contains(expectedDeviceCountHistoryArray));

	}

	@CsvSource({ "1", "2", "10" })
	@DisplayName("test Remove Client Socket Channel with ")
	@ParameterizedTest(name = " {0} sockets channel and expect device count change notifications")
	public void testRemoveClientSocketChannel(int noOfSocketsToBeTested) throws IOException {
		List<SocketChannelStub> addedSocketList = new ArrayList<>();
		Integer[] expectedDeviceCountHistoryArray = new Integer[noOfSocketsToBeTested * 2];
		for (int i = 1; i <= noOfSocketsToBeTested; i++) {
			expectedDeviceCountHistoryArray[i - 1] = i;
			Device device = new DeviceImpl("manufacturer", "modelId", "version", Integer.toString(i));
			SocketChannelStub socketChannel = new SocketChannelStub(device);
			addedSocketList.add(socketChannel);
			tcpServerClientSocketPool.addSocketChannel(device, socketChannel);
		}
		for (int i = 0; i < addedSocketList.size(); i++) {
			tcpServerClientSocketPool.removeClientSocketChannel(addedSocketList.get(i));
			expectedDeviceCountHistoryArray[noOfSocketsToBeTested + i] = noOfSocketsToBeTested - (i + 1);
		}
		assertEquals(0, tcpServerClientSocketPool.getDevices().size());
		assertThat(socketChangeListenerSpy.deviceCountChangeHistory, contains(expectedDeviceCountHistoryArray));
	}

	@CsvSource({ "1", "2", "10" })
	@DisplayName("test Close All Client Socket Channel with ")
	@ParameterizedTest(name = " {0} sockets channel and expect device count change notifications")
	public void testCloseAllClientSockets(int noOfSocketsToBeTested) throws IOException {
		List<SocketChannelStub> addedSocketList = new ArrayList<>();
		Integer[] expectedDeviceCountHistoryArray = new Integer[noOfSocketsToBeTested + 1];
		for (int i = 1; i <= noOfSocketsToBeTested; i++) {
			expectedDeviceCountHistoryArray[i - 1] = i;
			Device device = new DeviceImpl("manufacturer", "modelId", "version", Integer.toString(i));
			SocketChannelStub socketChannel = new SocketChannelStub(device);
			addedSocketList.add(socketChannel);
			tcpServerClientSocketPool.addSocketChannel(device, socketChannel);
		}
		expectedDeviceCountHistoryArray[noOfSocketsToBeTested] = 0;
		tcpServerClientSocketPool.closeAllClientSockets();
		assertEquals(0, tcpServerClientSocketPool.getDevices().size());
		assertThat(socketChangeListenerSpy.deviceCountChangeHistory, contains(expectedDeviceCountHistoryArray));
	}

	private class SocketChangeListenerSpy implements SocketChangeListener {

		private final List<Integer> deviceCountChangeHistory = new ArrayList<>();

		@Override
		public void handleChangedCount(ServerSocketToDeviceModel serverSocketToDeviceModel, int deviceCount) {
			deviceCountChangeHistory.add(deviceCount);
		}

	}

	private class SocketChannelStub extends SocketChannel {

		private final Device device;

		public SocketChannelStub(Device device) {
			super(null);
			this.device = device;
		}

		@Override
		public <T> T getOption(SocketOption<T> name) throws IOException {
			return null;
		}

		@Override
		public Set<SocketOption<?>> supportedOptions() {
			return null;
		}

		@Override
		public SocketChannel bind(SocketAddress local) throws IOException {
			return null;
		}

		@Override
		public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
			return null;
		}

		@Override
		public SocketChannel shutdownInput() throws IOException {
			return null;
		}

		@Override
		public SocketChannel shutdownOutput() throws IOException {
			return null;
		}

		@Override
		public Socket socket() {
			return new Socket();
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public boolean isConnectionPending() {
			return false;
		}

		@Override
		public boolean connect(SocketAddress remote) throws IOException {
			return false;
		}

		@Override
		public boolean finishConnect() throws IOException {
			return false;
		}

		@Override
		public SocketAddress getRemoteAddress() throws IOException {
			return null;
		}

		@Override
		public int read(ByteBuffer dst) throws IOException {
			return 0;
		}

		@Override
		public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
			return 0;
		}

		@Override
		public int write(ByteBuffer src) throws IOException {
			return 0;
		}

		@Override
		public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
			return 0;
		}

		@Override
		public SocketAddress getLocalAddress() throws IOException {
			return null;
		}

		@Override
		protected void implCloseSelectableChannel() throws IOException {
		}

		@Override
		protected void implConfigureBlocking(boolean block) throws IOException {
		}

	}

}
