/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task;

import static com.hpe.iot.dc.util.UtilityLogger.exceptionStackToString;
import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.DeviceSocketData;

/**
 * @author sveera
 *
 */
public abstract class DeviceDataProcessingTask implements Runnable {

	private volatile boolean isTaskRunnable;
	private final long pollingPeriod;
	private final LoggerService loggerService;
	protected final ServerSocketToDeviceModel serverSocketToDeviceModel;
	private final BlockingQueue<DeviceSocketData> liveSocketsData;

	public DeviceDataProcessingTask(long pollingPeriod, LoggerService loggerService,
			ServerSocketToDeviceModel serverSocketToDeviceModel) {
		super();
		this.isTaskRunnable = true;
		this.pollingPeriod = pollingPeriod;
		this.loggerService = loggerService;
		this.serverSocketToDeviceModel = serverSocketToDeviceModel;
		this.liveSocketsData = new LinkedTransferQueue<>();
	}

	public void addLiveSocketData(DeviceSocketData deviceSocketData) throws InterruptedException {
		this.liveSocketsData.put(deviceSocketData);
	}

	public void stopDataProcessingTask() {
		this.isTaskRunnable = false;
	}

	@Override
	public void run() {
		try {
			while (isTaskRunnable) {
				DeviceSocketData deviceSocketData = liveSocketsData.poll(pollingPeriod, MILLISECONDS);
				if (deviceSocketData == null)
					continue;
				tryProcessingSocketData(deviceSocketData);
			}
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
		}
	}

	private void tryProcessingSocketData(DeviceSocketData deviceSocketData) {
		try {
			processSocketData(deviceSocketData);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			loggerService.log(
					new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),
							serverSocketToDeviceModel.getModelId(), serverSocketToDeviceModel.getVersion()),
					exceptionStackToString(e));
		}
	}

	abstract protected void processSocketData(DeviceSocketData deviceSocketData) throws IOException;
}
