/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.task;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import com.hpe.iot.dc.tcp.southbound.service.inflow.DeviceSocketData;

/**
 * @author sveera
 *
 */
public abstract class DeviceDataProcessingTask implements Runnable {

	private final BlockingQueue<DeviceSocketData> liveSocketsData;
	private volatile boolean isTaskRunnable;
	private final long pollingPeriod;

	public DeviceDataProcessingTask(final long pollingPeriod) {
		super();
		this.pollingPeriod = pollingPeriod;
		this.liveSocketsData = new LinkedTransferQueue<>();
		this.isTaskRunnable = true;

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
		}
	}

	abstract protected void processSocketData(DeviceSocketData deviceSocketData) throws IOException;
}
