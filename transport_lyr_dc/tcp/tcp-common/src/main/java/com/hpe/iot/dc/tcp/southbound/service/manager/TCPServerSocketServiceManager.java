package com.hpe.iot.dc.tcp.southbound.service.manager;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.dc.northbound.component.manager.outflow.NorthBoundDownlinkComponentManager;
import com.hpe.iot.dc.tcp.component.model.TCPDCComponentModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.impl.TCPServerSocketService;
import com.hpe.iot.dc.tcp.southbound.service.inflow.TCPDataProcessingService;
import com.hpe.iot.dc.tcp.southbound.service.inflow.TCPServerSocketReader;
import com.hpe.iot.dc.tcp.southbound.service.inflow.TCPServerSocketReaderImpl;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.DeviceDataProcessingTaskFactory;
import com.hpe.iot.dc.tcp.southbound.service.inflow.task.factory.DeviceDataProcessingTaskFactoryInput;
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter;
import com.hpe.iot.dc.tcp.southbound.socket.TCPServerSocketServiceProvider;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.factory.TCPServerClientSocketPoolFactory;

/**
 * @author sveera
 *
 */
public class TCPServerSocketServiceManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<ServerSocketToDeviceModel, TCPServerSocketService> tcpServerSocketServices;
	private final TCPServerSocketServiceProvider tcpServerSocketServiceProvider;
	private final ManagedExecutorService managedExecutorService;
	private final NorthBoundDownlinkComponentManager northBoundDownlinkComponentManager;
	private final TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory;
	private final DeviceDataProcessingTaskFactory deviceDataProcessingTaskFactory;

	public TCPServerSocketServiceManager(NorthBoundDownlinkComponentManager northBoundDownlinkComponentManager,
			TCPServerSocketServiceProvider tcpServerSocketServiceProvider,
			TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory,
			DeviceDataProcessingTaskFactory deviceDataProcessingTaskFactory,
			ManagedExecutorService managedExecutorService) {
		super();
		this.tcpServerSocketServices = new ConcurrentHashMap<>();
		this.northBoundDownlinkComponentManager = northBoundDownlinkComponentManager;
		this.tcpServerSocketServiceProvider = tcpServerSocketServiceProvider;
		this.tcpServerClientSocketPoolFactory = tcpServerClientSocketPoolFactory;
		this.deviceDataProcessingTaskFactory = deviceDataProcessingTaskFactory;
		this.managedExecutorService = managedExecutorService;
	}

	public void createTCPServerSocketService(final ServerSocketToDeviceModel serverSocketToDeviceModel,
			final TCPDCComponentModel dcComponentModel, final ServerClientSocketPool tcpServerClientSocketPool,
			final TCPServerSocketWriter tcpServerSocketWriter, final LoggerService loggerService) throws IOException {
		TCPServerSocketService tcpServerSocketService = tcpServerSocketServices.get(serverSocketToDeviceModel);
		if (tcpServerSocketService != null) {
			logger.warn("Port already in use " + serverSocketToDeviceModel);
			throw new RuntimeException("Port already under usage : " + serverSocketToDeviceModel);
		}
		ServerSocketChannel serverSocketChannel = tcpServerSocketServiceProvider.getServerSocketChannel(
				serverSocketToDeviceModel.getPortNumber(), serverSocketToDeviceModel.getBoundLocalAddress(),
				serverSocketToDeviceModel.getTCPOptions().getSocketBacklogCount());
		DeviceDataProcessingTask deviceDataProcessingTask = deviceDataProcessingTaskFactory
				.createDeviceDataProcessingTask(new DeviceDataProcessingTaskFactoryInput(loggerService,
						serverSocketToDeviceModel, tcpServerClientSocketPool, dcComponentModel));
		TCPDataProcessingService tcpDataProcessingService = new TCPDataProcessingService(deviceDataProcessingTask,
				managedExecutorService);
		TCPServerSocketReader tcpServerSocketReader = new TCPServerSocketReaderImpl(managedExecutorService,
				tcpServerClientSocketPool, tcpDataProcessingService, serverSocketToDeviceModel);
		TCPServerSocketService newTCPServerSocketService = new TCPServerSocketService(serverSocketChannel,
				managedExecutorService, tcpServerSocketReader, serverSocketToDeviceModel);
		newTCPServerSocketService.startTCPServerSocketService();
		tcpServerSocketServices.put(serverSocketToDeviceModel, newTCPServerSocketService);
		logger.info("Created new TCPServerSocketService for Server socket Model " + serverSocketToDeviceModel);
		northBoundDownlinkComponentManager
				.addNorthBoundDCComponentModel(dcComponentModel.getNorthBoundDCComponentModel());
	}

	public void removeTCPServerSocketService(ServerSocketToDeviceModel portToDeviceModel) throws IOException {
		stopTCPServerSocketService(portToDeviceModel);
		logger.info("Stopped and removed TCPServerSocketServcie for " + portToDeviceModel);
	}

	public void startTCPServerSocketService(ServerSocketToDeviceModel portToDeviceModel) throws IOException {
		TCPServerSocketService tcpServerSocketService = tcpServerSocketServices.get(portToDeviceModel);
		if (tcpServerSocketService == null) {
			logger.warn("TCP Server SocketService not found " + portToDeviceModel);
			return;
		}
		tcpServerSocketService.startTCPServerSocketService();
	}

	public void stopTCPServerSocketService(ServerSocketToDeviceModel portToDeviceModel) throws IOException {
		TCPServerSocketService tcpServerSocketService = tcpServerSocketServices.get(portToDeviceModel);
		if (tcpServerSocketService == null) {
			logger.warn("TCP Server SocketService not found " + portToDeviceModel);
			return;
		}
		logger.info("Stopping TCPServerSocketServcie for [ " + portToDeviceModel.getManufacturer() + ":"
				+ portToDeviceModel.getModelId() + ":" + portToDeviceModel.getBoundLocalAddress() + ":"
				+ portToDeviceModel.getPortNumber() + " ]");
		northBoundDownlinkComponentManager.removeNorthBoundDCComponentModel(new DeviceModelImpl(
				portToDeviceModel.getManufacturer(), portToDeviceModel.getModelId(), portToDeviceModel.getVersion()));
		tcpServerClientSocketPoolFactory.removeServerClientSocketPool(portToDeviceModel);
		tcpServerSocketService.stopTCPServerSocketService();
		logger.info("Stopped TCPServerSocketServcie for [ " + portToDeviceModel.getManufacturer() + ":"
				+ portToDeviceModel.getModelId() + ":" + portToDeviceModel.getBoundLocalAddress() + ":"
				+ portToDeviceModel.getPortNumber() + " ]");
		tcpServerSocketServices.remove(portToDeviceModel);
	}

	public void stopAllTCPServerSocketServices() throws IOException {
		for (Map.Entry<ServerSocketToDeviceModel, TCPServerSocketService> tcpSocketServiceEntry : tcpServerSocketServices
				.entrySet()) {
			stopTCPServerSocketService(tcpSocketServiceEntry.getKey());
		}
	}

	public Set<ServerSocketToDeviceModel> getRunningServerSocketServices() {
		return tcpServerSocketServices.keySet();
	}

}
