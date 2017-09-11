/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedTransferQueue;

import javax.enterprise.concurrent.ManagedExecutorService;

/**
 * @author sveera
 *
 */
public class MqttMessageHandlerService implements Callable<Void> {

	private final MqttMessageHandler mqttMessageHandler;
	private final ManagedExecutorService managedExecutorService;
	private final BlockingQueue<ReceivedMqttMessage> mqttBrokerData;
	private final long pollingPeriod = 10l;
	private boolean isServiceRunnable;

	public MqttMessageHandlerService(MqttMessageHandler mqttMessageHandler,
			ManagedExecutorService managedExecutorService) {
		super();
		this.mqttMessageHandler = mqttMessageHandler;
		this.managedExecutorService = managedExecutorService;
		this.mqttBrokerData = new LinkedTransferQueue<>();
	}

	@Override
	public Void call() throws Exception {
		try {
			while (isServiceRunnable) {
				ReceivedMqttMessage mqttData = mqttBrokerData.poll(pollingPeriod, MILLISECONDS);
				if (mqttData == null)
					continue;
				mqttMessageHandler.processMqttData(mqttData);
			}
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
		}
		return null;
	}

	public void startService() {
		isServiceRunnable = true;
		managedExecutorService.submit(this);
	}

	public void stopService() {
		isServiceRunnable = false;
	}

	public void processMqttData(ReceivedMqttMessage mqttData) throws Exception {
		this.mqttBrokerData.put(mqttData);
	}

}
