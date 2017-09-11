/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

/**
 * @author sveera 
 */
public class ReceivedMqttMessage {

	private final String mqttTopic;
	private final String mqttMessage;

	public ReceivedMqttMessage(String mqttTopic, String mqttMessage) {
		super();
		this.mqttTopic = mqttTopic;
		this.mqttMessage = mqttMessage;
	}

	public String getMqttTopic() {
		return mqttTopic;
	}

	public String getMqttMessage() {
		return mqttMessage;
	}

	@Override
	public String toString() {
		return "ReceivedMqttMessage [mqttTopic=" + mqttTopic + ", mqttMessage=" + mqttMessage + "]";
	}
}
