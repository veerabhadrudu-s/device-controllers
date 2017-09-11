package com.hpe.iot.dc.tcp.southbound.service.inflow;

import java.nio.channels.SocketChannel;

import javax.enterprise.concurrent.ManagedExecutorService;

import com.hpe.iot.dc.tcp.southbound.service.inflow.task.DeviceDataProcessingTask;

/**
 * @author sveera
 *
 */
public class TCPDataProcessingService {

	private final ManagedExecutorService managedExecutorService;
	private final DeviceDataProcessingTask deviceDataProcessingTask;

	public TCPDataProcessingService(DeviceDataProcessingTask deviceDataProcessingTask,
			ManagedExecutorService managedExecutorService) {
		super();
		this.managedExecutorService = managedExecutorService;
		this.deviceDataProcessingTask = deviceDataProcessingTask;
	}

	public void startDataProcessingService() {
		managedExecutorService.execute(deviceDataProcessingTask);
	}

	public void stopDataProcessingService() {
		this.deviceDataProcessingTask.stopDataProcessingTask();

	}

	public void processDeviceData(SocketChannel clientSocket, byte[] dataFromClientSocket) throws InterruptedException {
		this.deviceDataProcessingTask.addLiveSocketData(new DeviceSocketData(dataFromClientSocket, clientSocket));

	}
}
