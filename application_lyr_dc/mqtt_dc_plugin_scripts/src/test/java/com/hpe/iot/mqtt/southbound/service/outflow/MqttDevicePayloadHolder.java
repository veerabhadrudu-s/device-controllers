/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.outflow;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;

/**
 * @author sveera
 *
 */
public class MqttDevicePayloadHolder {

	private final BlockingQueue<ReceivedMqttMessage> mqttDeviceData;
	private final long pollingPeriod = 5000l;

	public MqttDevicePayloadHolder() {
		super();
		this.mqttDeviceData = new LinkedTransferQueue<>();
	}

	public ReceivedMqttMessage getMqttDeviceData() throws InterruptedException {
		return mqttDeviceData.poll(pollingPeriod, MILLISECONDS);
	}

	public void holdMqttDeviceData(ReceivedMqttMessage receivedMqttMessage) {
		mqttDeviceData.add(receivedMqttMessage);
	}
	
}
