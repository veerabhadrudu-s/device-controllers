/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
public class MqttMessageHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SouthboundService southboundService;
	private final JsonParser jsonParser = new JsonParser();

	public MqttMessageHandler(SouthboundService southboundService) {
		super();
		this.southboundService = southboundService;
	}

	public void processMqttData(ReceivedMqttMessage mqttData) {
		try {
			logger.trace("Received uplink message from device is " + mqttData.toString());
			String topicParts[] = mqttData.getMqttTopic().split("/");
			String manufacturer = topicParts[0], modelId = topicParts[1];
			JsonObject payload = (JsonObject) jsonParser.parse(mqttData.getMqttMessage());
			southboundService.processPayload(manufacturer, modelId, payload);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
		}
	}
}
