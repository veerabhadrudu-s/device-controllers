/**
 * 
 */
package com.hpe.iot.dc.tcp.client.device.counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sveera
 *
 */
public class DeviceIdCounter {

	private final long initializationValue;
	private final int limit;
	private AtomicLong currentValue;

	public DeviceIdCounter(long initializationValue, int limit) {
		super();
		this.initializationValue = initializationValue;
		this.limit = limit;
		currentValue = new AtomicLong(initializationValue);
	}

	public long getNextDeviceID() {
		long currentValue = this.currentValue.getAndIncrement();
		if (currentValue > (initializationValue + limit)) {
			throw new RuntimeException("Counter reached to maximum limit");
		}
		return currentValue;
	}
}

