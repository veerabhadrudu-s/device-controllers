/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.reader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.ClientDeviceData;
import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;
import com.hpe.iot.dc.tcp.client.notify.ClientHandShakeNotifier;
import com.hpe.iot.dc.tcp.client.payload.converter.ServerToClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.settings.DeviceClientSettings;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManager;
import com.hpe.iot.dc.tcp.client.writer.ClientSocketWriter;

/**
 * @author sveera
 *
 */
public class ClientSocketReaderRunner implements Callable<Void> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ServerToClientMessageGenerator severToClientMessageGenerator;
	private final ClientSocketWriter clientSocketWriter;
	private final ClientHandShakeNotifier clientHandShakeNotifier;
	private final ClientSocketManager clientSocketManager;
	private final DeviceClientSettings deviceClientSettings;
	private final int index;
	private final Selector selector;
	private boolean isReaderRunnable;
	private int bufferSizeInBytes = 1024;

	public ClientSocketReaderRunner(ServerToClientMessageGenerator severToClientMessageGenerator,
			ClientSocketWriter clientSocketWriter, ClientHandShakeNotifier clientHandShakeNotifier,
			ClientSocketManager clientSocketManager, DeviceClientSettings deviceClientSettings, int index)
			throws IOException {
		super();
		this.severToClientMessageGenerator = severToClientMessageGenerator;
		this.clientSocketWriter = clientSocketWriter;
		this.clientHandShakeNotifier = clientHandShakeNotifier;
		this.clientSocketManager = clientSocketManager;
		this.deviceClientSettings = deviceClientSettings;
		this.isReaderRunnable = true;
		this.index = index;
		this.selector = Selector.open();

	}

	public void stopSocketReader() {
		isReaderRunnable = false;
	}

	@Override
	public Void call() throws Exception {
		registerConnectedClientsForListening();
		readDataFromConnectedClients();
		return null;
	}

	private void registerConnectedClientsForListening() throws ClosedChannelException {
		for (DeviceSocketModel deviceSocketModel : clientSocketManager.getDeviceClients())
			deviceSocketModel.getSocketChannel().register(selector, SelectionKey.OP_READ);
	}

	private void readDataFromConnectedClients() throws IOException, InterruptedException {
		while (isReaderRunnable) {
			readClientSocketChannelsForData();
		}
	}

	private void readClientSocketChannelsForData() throws IOException, InterruptedException {
		int channelCount = selector.selectNow();
		if (channelCount > 0)
			logger.trace("No of clients can be read is : " + channelCount + " in " + toString());
		Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
		while (selectedKeys.hasNext()) {
			SelectionKey selectionKey = selectedKeys.next();
			readEachClientSocketChannel(selectionKey);
			selectedKeys.remove();
		}
	}

	private void readEachClientSocketChannel(SelectionKey selectionKey) throws IOException, InterruptedException {
		if (selectionKey.isReadable()) {
			SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
			readDataFromStreams(clientSocket, selectionKey);
		}

	}

	private void readDataFromStreams(SocketChannel clientSocket, SelectionKey selectionKey)
			throws IOException, InterruptedException {
		ByteBuffer readBuffer = ByteBuffer.allocate(bufferSizeInBytes);
		int read = clientSocket.read(readBuffer);
		if (read == 0) {
			logger.trace("Nothing to read from the connected socket " + clientSocket.toString() + " in " + toString());
			return;
		} else if (read > 0) {
			byte[] dataFromClientSocket = readValidDataFromBuffer(readBuffer, read);
			ClientDeviceData clientDeviceData = severToClientMessageGenerator.handleMessage(dataFromClientSocket);
			processDataFromClientSocket(clientDeviceData);
		} else if (read == -1) {
			logger.info("Nothing to read from the client socket " + clientSocket.toString()
					+ " due to closed connection in " + toString());
		}
	}

	private byte[] readValidDataFromBuffer(ByteBuffer readBuffer, int read) {
		readBuffer.flip();
		byte[] data = new byte[read];
		readBuffer.get(data, 0, read);
		return data;
	}

	private void processDataFromClientSocket(ClientDeviceData clientDeviceData)
			throws IOException, InterruptedException {
		if (clientDeviceData.getMessagetype() != null && deviceClientSettings.getHandShakeResponseMsgType() != null
				&& clientDeviceData.getMessagetype().equals(deviceClientSettings.getHandShakeResponseMsgType()))
			handleDataForHandShakeResponseType(clientDeviceData);
		else {
			clientSocketWriter.pushDataUsingClients(clientDeviceData.getMessagetype(),
					clientSocketManager.getDeviceClient(clientDeviceData.getDeviceId()));
		}
	}

	private void handleDataForHandShakeResponseType(ClientDeviceData clientDeviceData) {
		clientHandShakeNotifier.handshakeCompleted(clientDeviceData.getDeviceId());
	}

	@Override
	public String toString() {
		return "ClientSocketReaderRunner [index=" + index + "]";
	}

}
