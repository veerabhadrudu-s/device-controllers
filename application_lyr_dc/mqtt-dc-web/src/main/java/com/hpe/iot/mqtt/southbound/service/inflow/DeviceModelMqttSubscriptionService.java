/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface DeviceModelMqttSubscriptionService {

	void subscribeForDeviceModel(final DeviceModel deviceModel) throws MqttException;

	void unsubscribeForDeviceModel(final DeviceModel deviceModel) throws MqttException;

}
