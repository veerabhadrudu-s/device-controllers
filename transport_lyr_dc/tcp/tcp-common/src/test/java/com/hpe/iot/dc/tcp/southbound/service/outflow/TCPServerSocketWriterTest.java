/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.outflow;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class TCPServerSocketWriterTest {

	private static final byte[] DEVICE_MESSAGE = new byte[] { 72, 101, 108, 108, 111 };
	private TCPServerSocketWriter tcpServerSocketWriter;
	private ServerClientSocketPool tcpServerClientSocketPool;
	private Device connectedDevice = new DeviceStub();
	private SocketChannelSpy deviceSocketChannel = new SocketChannelSpy();

	@BeforeEach
	public void setUp() throws Exception {
		tcpServerClientSocketPool = new ServerClientSocketPoolStub(connectedDevice, deviceSocketChannel);
		tcpServerSocketWriter = new TCPServerSocketWriter(tcpServerClientSocketPool);
	}

	@Test
	@DisplayName("test Send Message")
	public void testSendMessage() {
		tcpServerSocketWriter.sendMessage(connectedDevice, DEVICE_MESSAGE);
		assertArrayEquals(DEVICE_MESSAGE, deviceSocketChannel.deviceData,
				"Expected and actula device messages are same");
	}

	private class DeviceStub implements Device {

		@Override
		public String getManufacturer() {
			return null;
		}

		@Override
		public String getModelId() {
			return null;
		}

		@Override
		public String getVersion() {
			return null;
		}

		@Override
		public String getDeviceId() {
			return null;
		}

	}

	private class SocketChannelSpy extends SocketChannel {

		private byte[] deviceData;

		protected SocketChannelSpy() {
			super(null);
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
			return null;
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
			deviceData = new byte[src.remaining()];
			src.get(deviceData);
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

	private class ServerClientSocketPoolStub implements ServerClientSocketPool {

		private Device mockDevice;
		private SocketChannel mockedSocketChannel;

		public ServerClientSocketPoolStub(Device mockDevice, SocketChannel mockedSocketChannel) {
			super();
			this.mockDevice = mockDevice;
			this.mockedSocketChannel = mockedSocketChannel;
		}

		@Override
		public Device getDevice(SocketChannel socketChannel) {
			return null;
		}

		@Override
		public List<Device> getDevices() {
			return null;
		}

		@Override
		public SocketChannel getClientSocket(Device device) {
			return device.equals(mockDevice) ? mockedSocketChannel : null;
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
