/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.outflow;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.hpe.iot.mqtt.southbound.service.inflow.ReceivedMqttMessage;

/**
 * @author sveera
 *
 */
public class MqttDevicePayloadHolder {

	private final List<ReceivedMqttMessage> mqttDeviceData;
	private final CountDownLatch countDownLatch;

	public MqttDevicePayloadHolder(CountDownLatch countDownLatch) {
		super();
		this.mqttDeviceData = new LinkedList<>();
		this.countDownLatch = countDownLatch;
	}

	public List<ReceivedMqttMessage> getMqttDeviceData() throws InterruptedException {
		return mqttDeviceData;
	}

	public void holdMqttDeviceData(ReceivedMqttMessage receivedMqttMessage) {
		mqttDeviceData.add(receivedMqttMessage);
		countDownLatch.countDown();
	}

}
