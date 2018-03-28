/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.handson.logger.service.LoggerService;
import com.handson.logger.service.impl.Slf4jLoggerServiceAdaptee;
import com.hpe.iot.dc.executor.mock.MockManagedExecutorService;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModelDummy;
import com.hpe.iot.dc.tcp.southbound.service.impl.TCPServerSocketService;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.socket.TCPServerSocketServiceProvider;
import com.hpe.iot.dc.tcp.southbound.socket.impl.TCPServerSocketServiceProviderImpl;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class TCPServerSocketReaderImplTest {

	private static final int NO_OF_CLIENTS = 10;
	private static final int NO_OF_TIMES_MSGS_TO_BE_WRITTEN = 2;
	private static final int TOTAL_NO_OF_MSGS_FROM_ALL_CLIENTS = NO_OF_CLIENTS * NO_OF_TIMES_MSGS_TO_BE_WRITTEN;
	private static final int REMOVE_CLIENT_SOCKT_CHNL_CALLS_COUNT = NO_OF_CLIENTS;
	private static final int TOTAL_ASYNC_CALLS_COUNT = TOTAL_NO_OF_MSGS_FROM_ALL_CLIENTS
			+ REMOVE_CLIENT_SOCKT_CHNL_CALLS_COUNT;
	private final CountDownLatch countDownLatch = new CountDownLatch(TOTAL_ASYNC_CALLS_COUNT);
	private final ManagedExecutorService managedExecutorService = new MockManagedExecutorService();
	private final ServerSocketToDeviceModel serverSocketToDeviceModel = new ServerSocketToDeviceModelDummy();
	private final ServerClientSocketPoolSpy serverClientSocketPoolSpy = new ServerClientSocketPoolSpy(countDownLatch);
	private final DeviceDataProcessingTaskSpy deviceDataProcessingTaskSpy = new DeviceDataProcessingTaskSpy(1,
			new Slf4jLoggerServiceAdaptee(), serverSocketToDeviceModel, countDownLatch);
	private final TCPDataProcessingServiceSpy tcpDataProcessingServiceSpy = new TCPDataProcessingServiceSpy(
			deviceDataProcessingTaskSpy, managedExecutorService);
	private TCPServerSocketReaderImpl tcpServerSocketReaderImpl;

	@BeforeEach
	public void setUp() throws Exception {
		tcpServerSocketReaderImpl = new TCPServerSocketReaderImpl(managedExecutorService, serverClientSocketPoolSpy,
				tcpDataProcessingServiceSpy, serverSocketToDeviceModel);
	}

	@AfterEach
	public void tearDown() {
		managedExecutorService.shutdownNow();
	}

	@Test
	public void testTCPServerSocketReaderImpl() {
		assertNotNull(tcpServerSocketReaderImpl,
				tcpServerSocketReaderImpl.getClass().getSimpleName() + " cannot be null");
	}

	@Nested
	public class TCPServerSocketReaderImpl_Test_With_Connected_Clients {

		private static final int PORT_NUMBER = 2000;
		private static final String BIND_IP_ADDRESS = "localhost";
		private TCPServerSocketServiceProvider tcpServerSocketServiceProvider = new TCPServerSocketServiceProviderImpl();
		private TCPServerSocketService tcpServerSocketService;

		@BeforeEach
		public void setUp() throws Exception {
			tcpServerSocketService = new TCPServerSocketService(
					tcpServerSocketServiceProvider.getServerSocketChannel(PORT_NUMBER, BIND_IP_ADDRESS, 10000),
					managedExecutorService, tcpServerSocketReaderImpl, serverSocketToDeviceModel);
		}

		@Test
		@DisplayName("test Connected clients socket data been read")
		public void testConnectedClientsSocketDataBeenRead() throws InterruptedException {
			tcpServerSocketService.startTCPServerSocketService();
			writeDataUsingClients();
			countDownLatch.await(10000, MILLISECONDS);
			tcpServerSocketService.stopTCPServerSocketService();
			assertTrue(tcpDataProcessingServiceSpy.isStartDataProcessingServiceInvoked,
					TCPDataProcessingService.class.getSimpleName()
							+ " startDataProcessingService method should be invoked");
			assertEquals(TOTAL_NO_OF_MSGS_FROM_ALL_CLIENTS, tcpDataProcessingServiceSpy.processDeviceDataCounter,
					"Actual and expected no of processed messages are not equal");
			assertEquals(TOTAL_NO_OF_MSGS_FROM_ALL_CLIENTS, deviceDataProcessingTaskSpy.deviceSocketData.size(),
					"Actual and expected no of processed messages are not equal");
			assertEquals(REMOVE_CLIENT_SOCKT_CHNL_CALLS_COUNT, serverClientSocketPoolSpy.removedSocketChannels.size(),
					"Actual and expected no of remove client socket channel count are not equal");
			assertTrue(tcpDataProcessingServiceSpy.isStopDataProcessingServiceInvoked,
					TCPDataProcessingService.class.getSimpleName()
							+ " stopDataProcessingService method should be invoked");
			assertTrue(serverClientSocketPoolSpy.isCloseAllClientSocketsInvoked,
					ServerClientSocketPool.class.getSimpleName() + " closeAllClientSockets method should be invoked");
		}

		private void writeDataUsingClients() {
			Callable<Void> callable = () -> {
				List<SocketChannel> connectedClinets = connectClients();
				for (int i = 0; i < NO_OF_TIMES_MSGS_TO_BE_WRITTEN; i++)
					writeData(connectedClinets);
				closeClients(connectedClinets);
				return null;
			};
			managedExecutorService.submit(callable);
		}

		private List<SocketChannel> connectClients() throws IOException {
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

		private void writeData(List<SocketChannel> connectedClients) throws IOException, InterruptedException {
			for (SocketChannel socketChannel : connectedClients)
				postMessage("Hello Server".getBytes(), socketChannel);
		}

		private void postMessage(byte[] message, SocketChannel socketChannel) throws IOException, InterruptedException {
			ByteBuffer byteBuffer = ByteBuffer.allocate(message.length);
			byteBuffer.put(message);
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			byteBuffer.clear();
			new CountDownLatch(1).await(500 / NO_OF_CLIENTS, MILLISECONDS);
		}

		private void closeClients(List<SocketChannel> connectedClinets) {
			for (SocketChannel socketChannel : connectedClinets)
				tryClosingClientSocketChannel(socketChannel);

		}

		protected void tryClosingClientSocketChannel(SocketChannel socketChannel) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				logExceptionStackTrace(e, getClass());
			}
		}

	}

	private class ServerClientSocketPoolSpy extends DefaultTCPServerClientSocketPool {
		private final CountDownLatch countDownLatch;
		private final List<SocketChannel> removedSocketChannels = new ArrayList<>();
		private boolean isCloseAllClientSocketsInvoked;

		public ServerClientSocketPoolSpy(CountDownLatch countDownLatch) {
			super();
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void removeClientSocketChannel(SocketChannel socketChannel) {
			super.removeClientSocketChannel(socketChannel);
			removedSocketChannels.add(socketChannel);
			countDownLatch.countDown();
		}

		@Override
		public void closeAllClientSockets() {
			super.closeAllClientSockets();
			isCloseAllClientSocketsInvoked = true;
		}
	}

	private class TCPDataProcessingServiceSpy extends TCPDataProcessingService {

		private boolean isStartDataProcessingServiceInvoked;
		private boolean isStopDataProcessingServiceInvoked;
		private int processDeviceDataCounter;

		public TCPDataProcessingServiceSpy(DeviceDataProcessingTask deviceDataProcessingTask,
				ManagedExecutorService managedExecutorService) {
			super(deviceDataProcessingTask, managedExecutorService);
		}

		@Override
		public void startDataProcessingService() {
			super.startDataProcessingService();
			this.isStartDataProcessingServiceInvoked = true;
		}

		public void stopDataProcessingService() {
			super.stopDataProcessingService();
			this.isStopDataProcessingServiceInvoked = true;
		}

		public void processDeviceData(SocketChannel clientSocket, byte[] dataFromClientSocket)
				throws InterruptedException {
			super.processDeviceData(clientSocket, dataFromClientSocket);
			this.processDeviceDataCounter++;
		}

	}

	private class DeviceDataProcessingTaskSpy extends DeviceDataProcessingTask {

		private final List<DeviceSocketData> deviceSocketData = new ArrayList<>();
		private final CountDownLatch countDownLatch;

		public DeviceDataProcessingTaskSpy(long pollingPeriod, LoggerService loggerService,
				ServerSocketToDeviceModel serverSocketToDeviceModel, CountDownLatch countDownLatch) {
			super(pollingPeriod, loggerService, serverSocketToDeviceModel);
			this.countDownLatch = countDownLatch;
		}

		@Override
		protected void processSocketData(DeviceSocketData deviceSocketData) throws IOException {
			this.deviceSocketData.add(deviceSocketData);
			this.countDownLatch.countDown();
		}
	}

}
