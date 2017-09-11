package com.hpe.iot.dc.tcp.southbound.service.inflow;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class TCPServerSocketReader {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ManagedExecutorService managedExecutorService;
	private final ConnectedClientSocketListener connectedClientSocketListener;
	private final ServerClientSocketPool tcpServerClientSocketPool;
	private final Selector myClientSocketSelector;
	private final TCPDataProcessingService tcpDataReaderService;
	private final ServerSocketToDeviceModel portToDeviceModel;
	private final int bufferSizeInBytes;

	public TCPServerSocketReader(ManagedExecutorService managedExecutorService,
			ServerClientSocketPool tcpServerClientSocketPool, TCPDataProcessingService tcpDataReaderService,
			ServerSocketToDeviceModel portToDeviceModel) throws IOException {
		this.managedExecutorService = managedExecutorService;
		this.tcpServerClientSocketPool = tcpServerClientSocketPool;
		this.myClientSocketSelector = tcpServerClientSocketPool.getClientSocketSelector();
		this.tcpDataReaderService = tcpDataReaderService;
		this.portToDeviceModel = portToDeviceModel;
		this.bufferSizeInBytes = portToDeviceModel.getTCPOptions().getBufferCapacity();
		this.connectedClientSocketListener = new ConnectedClientSocketListener();

	}

	public void startClientCommunication() {
		tcpDataReaderService.startDataProcessingService();
		managedExecutorService.execute(connectedClientSocketListener);
	}

	public void stopClientCommunication() {
		tcpDataReaderService.stopDataProcessingService();
		connectedClientSocketListener.stopClientCommunication();
	}

	public void startCommunicatingWithClient(SocketChannel socketToClient) throws IOException {
		socketToClient.configureBlocking(false);
		socketToClient.register(myClientSocketSelector, SelectionKey.OP_READ);
	}

	private class ConnectedClientSocketListener implements Runnable {

		// private static final int TIME_INT_TO_INVSTGT_FOR_INFLOW_CLIENTS =
		// 10000;
		private volatile boolean isClientSocketListening;

		public ConnectedClientSocketListener() {
			super();
			this.isClientSocketListening = true;
		}

		@Override
		public void run() {
			logger.info("TCPServerSocketReader for server socket model " + portToDeviceModel
					+ " started to read client Sockets ");
			while (isClientSocketListening) {
				try {
					readClientSocketChannelsForData();
				} catch (Throwable e) {
					isClientSocketListening = false;
					logger.error("Failed to listen to client socket channels : ", e);
					logExceptionStackTrace(e, getClass());
				}

			}
		}

		private void readClientSocketChannelsForData() throws IOException {
			// int channelCount =
			// myClientSocketSelector.select(TIME_INT_TO_INVSTGT_FOR_INFLOW_CLIENTS);
			int channelCount = myClientSocketSelector.selectNow();
			if (channelCount > 0)
				logger.trace("No of clients wrote the data to server socket model - " + portToDeviceModel + " is : "
						+ channelCount);
			Iterator<SelectionKey> selectedKeys = myClientSocketSelector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey selectionKey = selectedKeys.next();
				readEachClientSocketChannel(selectionKey);
				selectedKeys.remove();
			}
		}

		private void readEachClientSocketChannel(SelectionKey selectionKey) throws IOException {
			logger.trace("Checking is readable Selection key : " + selectionKey.isReadable()
					+ " for client connected on server socket model - " + portToDeviceModel);
			if (selectionKey.isReadable()) {
				SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
				try {
					readDataFromStreams(clientSocket, selectionKey);
				} catch (IOException | InterruptedException e) {
					logger.error("Failed to read data from client socket channel ", e);
					closeSocketChannel(clientSocket);
					logExceptionStackTrace(e, getClass());
				}
			}

		}

		private void readDataFromStreams(SocketChannel clientSocket, SelectionKey selectionKey)
				throws IOException, InterruptedException {
			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSizeInBytes);
			int read = clientSocket.read(readBuffer);
			if (read == 0) {
				logger.trace("Nothing to read from the client socket connected on server socket model - "
						+ portToDeviceModel);
				if (!clientSocket.isConnected())
					releaseKeyDuetoClosedConnection(selectionKey, clientSocket);
				return;
			} else if (read > 0) {
				byte[] dataFromClientSocket = readValidDataFromBuffer(readBuffer, read);
				tcpDataReaderService.processDeviceData(clientSocket, dataFromClientSocket);
			} else if (read == -1) {
				logger.info("Nothing to read from the client socket " + clientSocket.toString()
						+ " due to closed connection on server port model - " + portToDeviceModel);
				releaseKeyDuetoClosedConnection(selectionKey, clientSocket);
			}
		}

		private byte[] readValidDataFromBuffer(ByteBuffer readBuffer, int read) {
			readBuffer.flip();
			byte[] data = new byte[read];
			readBuffer.get(data, 0, read);
			return data;
		}

		private void releaseKeyDuetoClosedConnection(SelectionKey selectionKey, SocketChannel clientSocket)
				throws IOException {
			selectionKey.cancel();
			closeSocketChannel(clientSocket);
			tcpServerClientSocketPool.removeClientSocketChannel(clientSocket);
		}

		private void closeSocketChannel(SocketChannel clientSocket) throws IOException {
			clientSocket.socket().close();
			clientSocket.close();
		}

		public void stopClientCommunication() {
			this.isClientSocketListening = false;
		}

	}
}
