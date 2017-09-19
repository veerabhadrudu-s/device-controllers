/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

/**
 * @author sveera 
 */
public class ReceivedMqttMessage {

	private final String mqttTopic;
	private final byte[] mqttMessage;

	public ReceivedMqttMessage(String mqttTopic, byte[] mqttMessage) {
		super();
		this.mqttTopic = mqttTopic;
		this.mqttMessage = mqttMessage;
	}

	public String getMqttTopic() {
		return mqttTopic;
	}

	public byte[] getMqttMessage() {
		return mqttMessage;
	}

	@Override
	public String toString() {
		return "ReceivedMqttMessage [mqttTopic=" + mqttTopic + ", mqttMessage=" + mqttMessage + "]";
	}
}
