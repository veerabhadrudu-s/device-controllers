/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.impl;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.executor.mock.MockManagedExecutorService;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModelDummy;
import com.hpe.iot.dc.tcp.southbound.service.inflow.TCPServerSocketReader;
import com.hpe.iot.dc.tcp.southbound.socket.TCPServerSocketServiceProvider;
import com.hpe.iot.dc.tcp.southbound.socket.impl.TCPServerSocketServiceProviderImpl;

/**
 * @author sveera
 *
 */
public class TCPServerSocketServiceTest {

	private static final int NO_OF_CLIENTS = 10;
	private static final int PORT_NUMBER = 2000;
	private static final String BIND_IP_ADDRESS = "localhost";
	private final TCPServerSocketReaderSpy tcpServerSocketReaderSpy = new TCPServerSocketReaderSpy();
	private final ManagedExecutorService managedExecutorService = new MockManagedExecutorService();
	private TCPServerSocketService tcpServerSocketService;

	@BeforeEach
	public void setUp() throws Exception {
		TCPServerSocketServiceProvider tcpServerSocketServiceProvider = new TCPServerSocketServiceProviderImpl();
		tcpServerSocketService = new TCPServerSocketService(
				tcpServerSocketServiceProvider.getServerSocketChannel(PORT_NUMBER, BIND_IP_ADDRESS, 1000),
				managedExecutorService, tcpServerSocketReaderSpy, new ServerSocketToDeviceModelDummy());
	}

	@AfterEach
	public void tearDown() {
		managedExecutorService.shutdown();
	}

	@Test
	@DisplayName("test TCPServerSocketService With Connected Client Sockets")
	public void testTCPServerSocketServiceWithConnectedClientSockets() throws IOException, InterruptedException {
		tcpServerSocketService.startTCPServerSocketService();
		List<SocketChannel> expectedConnectedClients = connectClients();
		new CountDownLatch(1).await(1000, MILLISECONDS);
		tcpServerSocketService.stopTCPServerSocketService();
		assertTrue(tcpServerSocketReaderSpy.isStartClientCommunicationInvoked,
				TCPServerSocketReader.class.getSimpleName()
						+ " startClientCommunication client method was not invoked");
		assertTrue(tcpServerSocketReaderSpy.isStopClientCommunicationInvoked,
				TCPServerSocketReader.class.getSimpleName() + " stopClientCommunication client method was not invoked");
		assertConnectClients(tcpServerSocketReaderSpy.clientSockets, expectedConnectedClients);
	}

	protected List<SocketChannel> connectClients() throws IOException {
		List<SocketChannel> connectedClients = new ArrayList<>();
		for (int i = 0; i < NO_OF_CLIENTS; i++) {
			SocketChannel socketChannel = SocketChannel.open();
			boolean isConnected = socketChannel.connect(new InetSocketAddress(BIND_IP_ADDRESS, PORT_NUMBER));
			if (!isConnected)
				socketChannel.finishConnect();
			connectedClients.add(socketChannel);
		}
		return connectedClients;
	}

	private void assertConnectClients(List<SocketChannel> expectedconnectedClients,
			List<SocketChannel> actualconnectedClients) {
		for (int i = 0; i < expectedconnectedClients.size(); i++)
			assertEquals(expectedconnectedClients.get(i).socket().getLocalPort(),
					actualconnectedClients.get(i).socket().getPort());
	}

	private class TCPServerSocketReaderSpy implements TCPServerSocketReader {

		private boolean isStartClientCommunicationInvoked;
		private boolean isStopClientCommunicationInvoked;
		private List<SocketChannel> clientSockets = new ArrayList<>();

		@Override
		public void startClientCommunication() {
			isStartClientCommunicationInvoked = true;
		}

		@Override
		public void stopClientCommunication() {
			isStopClientCommunicationInvoked = true;
		}

		@Override
		public void startCommunicatingWithClient(SocketChannel socketToClient) throws IOException {
			clientSockets.add(socketToClient);
		}
	}

}
