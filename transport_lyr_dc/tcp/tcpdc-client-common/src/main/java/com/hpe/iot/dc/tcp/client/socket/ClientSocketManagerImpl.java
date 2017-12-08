/**
 * 
 */
package com.hpe.iot.dc.tcp.client.socket;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.device.counter.DeviceIdCounter;
import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;
import com.hpe.iot.dc.tcp.client.notify.ClientHandShakeNotifier;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientHandlerSettings;

/**
 * @author sveera
 *
 */
public class ClientSocketManagerImpl implements ClientSocketManager, ClientHandShakeNotifier {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClientHandlerSettings clientRunnerSettings;
	private final DeviceIdCounter deviceIdCounter;
	private final int index;
	private final Map<Long, DeviceSocketModel> deviceClients;
	private final Map<Long, DeviceSocketModel> handShakedDeviceClients;

	public ClientSocketManagerImpl(ClientHandlerSettings clientRunnerSettings, DeviceIdCounter deviceIdCounter,
			int index) {
		super();
		this.clientRunnerSettings = clientRunnerSettings;
		this.deviceIdCounter = deviceIdCounter;
		this.index = index;
		this.deviceClients = new ConcurrentHashMap<>();
		this.handShakedDeviceClients = new ConcurrentHashMap<>();
	}

	@Override
	public List<DeviceSocketModel> getDeviceClients() {
		return new ArrayList<>(deviceClients.values());
	}

	@Override
	public DeviceSocketModel getDeviceClient(long deviceId) {
		return deviceClients.get(deviceId);
	}

	@Override
	public List<DeviceSocketModel> getHandshakedClients() {
		return new ArrayList<>(handShakedDeviceClients.values());
	}

	@Override
	public void stopAllClients() {
		logger.debug("Trying to close all connected Clients.");
		for (DeviceSocketModel deviceClient : deviceClients.values()) {
			try {
				deviceClient.getSocketChannel().socket().close();
				deviceClient.getSocketChannel().close();
			} catch (IOException e) {
				logExceptionStackTrace(e, getClass());
			}
		}
	}

	@Override
	public void handshakeCompleted(long... deviceIds) {
		for (long deviceId : deviceIds) {
			logger.trace("Completed handshake for device Id " + deviceId);
			DeviceSocketModel deviceSocketModel = deviceClients.get(deviceId);
			if (deviceSocketModel != null)
				handShakedDeviceClients.put(deviceId, deviceSocketModel);
		}
	}

	@Override
	public void connectClients() throws IOException, InterruptedException {
		InetSocketAddress remoteSocket = new InetSocketAddress(
				InetAddress.getByName(clientRunnerSettings.getServerIp()), clientRunnerSettings.getServerPort());
		for (int clientCounter = 0; clientCounter < clientRunnerSettings.getNoOfClients(); clientCounter++) {
			long nextDeviceID = deviceIdCounter.getNextDeviceID();
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(remoteSocket);
			deviceClients.put(nextDeviceID, new DeviceSocketModel(nextDeviceID, socketChannel));
			logger.trace("Connecting in ClientSocketManager - " + index + " with connected client Device ID "
					+ nextDeviceID);
			Thread.sleep(50);
		}
		waitForAllClientsToConnect();
	}

	private void waitForAllClientsToConnect() throws IOException, InterruptedException {
		int allClientsCount;
		int waitIteration = 0;
		int clientsConnected;
		do {
			List<Long> faliedClients = new ArrayList<>();
			allClientsCount = deviceClients.size();
			clientsConnected = 0;
			for (DeviceSocketModel deviceClient : deviceClients.values()) {
				try {
					if (isConnectionCompleted(deviceClient))
						clientsConnected++;
				} catch (IOException e) {
					clientsConnected++;
					faliedClients.add(deviceClient.getDeviceId());
					logger.error("Failed to connect client socket with local port "
							+ deviceClient.getSocketChannel().socket().getLocalPort());
					logExceptionStackTrace(e, getClass());
				}

			}
			logger.debug(toString() + "Completed " + waitIteration
					+ " wait cycle for all clients connection completion with pending clients to connect "
					+ (allClientsCount - clientsConnected));
			for (Long failedDevice : faliedClients)
				deviceClients.remove(failedDevice);
			Thread.sleep(5000);
			waitIteration++;
		} while (clientsConnected != allClientsCount && waitIteration <= clientRunnerSettings.getWaitIteration());

		logger.debug(toString() + "---------------------------------------------------------------------------------");
	}

	private boolean isConnectionCompleted(DeviceSocketModel deviceClient) throws IOException {
		boolean isConnected = false;
		if (deviceClient.getSocketChannel().isConnected()) {
			isConnected = true;
		} else {
			deviceClient.getSocketChannel().finishConnect();
			logger.trace(toString() + "Finished Connecting client for device ID " + deviceClient.getDeviceId()
					+ " on local port " + deviceClient.getSocketChannel().socket().getLocalPort());
		}
		return isConnected;
	}

	@Override
	public String toString() {
		return "ClientSocketManagerImpl [index=" + index + "]";
	}

}
