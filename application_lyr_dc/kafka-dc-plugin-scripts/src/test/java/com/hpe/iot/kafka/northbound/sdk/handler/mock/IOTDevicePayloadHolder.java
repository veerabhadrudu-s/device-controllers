/**
 * 
 */
package com.hpe.iot.kafka.northbound.sdk.handler.mock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class IOTDevicePayloadHolder {

	private final BlockingQueue<DeviceInfo> iotDeviceData;
	private final long pollingPeriod = 5000l;

	public IOTDevicePayloadHolder() {
		super();
		this.iotDeviceData = new LinkedTransferQueue<>();
	}

	public DeviceInfo getIOTDeviceData() throws InterruptedException {
		return iotDeviceData.poll(pollingPeriod, MILLISECONDS);
	}

	public void holdIOTDeviceData(DeviceInfo deviceInfo) {
		iotDeviceData.add(deviceInfo);
	}
	
}
