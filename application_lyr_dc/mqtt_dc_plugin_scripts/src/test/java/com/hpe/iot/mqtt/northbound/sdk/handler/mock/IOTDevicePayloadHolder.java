/**
 * 
 */
package com.hpe.iot.mqtt.northbound.sdk.handler.mock;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class IOTDevicePayloadHolder {

	private final List<DeviceInfo> iotDeviceData;
	private final CountDownLatch countDownLatch;

	public IOTDevicePayloadHolder(CountDownLatch countDownLatch) {
		this.iotDeviceData = new LinkedList<>();
		this.countDownLatch = countDownLatch;
	}

	public List<DeviceInfo> getIOTDeviceData() throws InterruptedException {
		return iotDeviceData;
	}

	public void holdIOTDeviceData(DeviceInfo deviceInfo) {
		iotDeviceData.add(deviceInfo);
		countDownLatch.countDown();
	}

}
