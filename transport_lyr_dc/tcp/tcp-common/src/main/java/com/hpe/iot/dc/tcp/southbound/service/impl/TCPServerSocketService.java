package com.hpe.iot.dc.tcp.southbound.service.impl;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.TCPServerSocketReader;

/**
 * @author sveera
 *
 */
public class TCPServerSocketService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServerSocketListener serverSocketListener;
	private final ManagedExecutorService managedExecutorService;
	private final TCPServerSocketReader tcpServerSocketReader;
	private final ServerSocketToDeviceModel serverSocketToDeviceModel;

	public TCPServerSocketService(ServerSocketChannel serverSocketChannel,
			ManagedExecutorService managedExecutorService, TCPServerSocketReader tcpServerSocketReader,
			ServerSocketToDeviceModel serverSocketToDeviceModel) throws IOException {
		super();
		this.managedExecutorService = managedExecutorService;
		this.tcpServerSocketReader = tcpServerSocketReader;
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
		this.serverSocketListener = new ServerSocketListener(serverSocketChannel);
	}

	public void startTCPServerSocketService() {
		tcpServerSocketReader.startClientCommunication();
		managedExecutorService.execute(serverSocketListener);
	}

	public void stopTCPServerSocketService() {
		tcpServerSocketReader.stopClientCommunication();
		serverSocketListener.stopPortListening();
	}

	protected class ServerSocketListener implements Runnable {

		// private static final int POLLING_PERIOD_FOR_NEW_CONNECTIONS = 5000;

		private volatile boolean isPortListening;

		private final Selector myServerSocketSelector;
		private final ServerSocketChannel serverSocketChannel;

		public ServerSocketListener(ServerSocketChannel serverSocketChannel) throws IOException {
			super();
			this.isPortListening = true;
			this.serverSocketChannel = serverSocketChannel;
			this.myServerSocketSelector = Selector.open();
			this.serverSocketChannel.register(myServerSocketSelector, SelectionKey.OP_ACCEPT);
		}

		public void run() {
			logger.debug("TCPServerSocketService started to listen client Sockets on " + serverSocketToDeviceModel);
			do
				connectClientSockets();
			while (isPortListening);
		}

		private void connectClientSockets() {
			try {
				// int channelCount =
				// myServerSocketSelector.select(POLLING_PERIOD_FOR_NEW_CONNECTIONS);
				int channelCount = myServerSocketSelector.selectNow();
				if (channelCount > 0)
					logger.trace("No of Clients started connecting to server socket - " + serverSocketToDeviceModel
							+ " is : " + channelCount);
				Iterator<SelectionKey> selectedKeys = myServerSocketSelector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey selectionKey = selectedKeys.next();
					if (selectionKey.isAcceptable()) {
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
						SocketChannel socketToClient = serverSocketChannel.accept();
						socketToClient.configureBlocking(false);
						readDataUsingConnectedSocket(socketToClient);
					}
					selectedKeys.remove();
				}

			} catch (IOException e) {
				logger.error("Error processing TCP message", e);
				logExceptionStackTrace(e, getClass());
			} catch (Exception e) {
				logger.error("Error processing TCP message", e);
				logExceptionStackTrace(e, getClass());
			}

		}

		private void readDataUsingConnectedSocket(SocketChannel socketToClient) throws IOException {
			logger.trace("Client Started to Communicating to " + serverSocketToDeviceModel
					+ " over TCP with IP Address : " + socketToClient.socket().getRemoteSocketAddress() + " on port : "
					+ socketToClient.socket().getPort());
			tcpServerSocketReader.startCommunicatingWithClient(socketToClient);
		}

		public void stopPortListening() {
			isPortListening = false;
			try {
				myServerSocketSelector.close();
				serverSocketChannel.socket().close();
				serverSocketChannel.close();
			} catch (IOException e) {
				logger.error("Failed to close Server Socket resources " + e.getMessage());
				logExceptionStackTrace(e, getClass());
			}
		}
	}
}
