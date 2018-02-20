/**
 * 
 */
package com.hpe.iot.mqtt.southbound.service.inflow;

import static com.handson.iot.dc.util.UtilityLogger.exceptionStackToString;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
public class MqttMessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LoggerService loggerService;
	private final SouthboundService southboundService;

	public MqttMessageHandler(LoggerService loggerService, SouthboundService southboundService) {
		super();
		this.loggerService = loggerService;
		this.southboundService = southboundService;
	}

	public void processMqttData(ReceivedMqttMessage mqttData) {
		logger.trace("Received uplink message from device is " + mqttData.toString());
		String topicParts[] = mqttData.getMqttTopic().split("/");
		DeviceModel deviceModel = new DeviceModelImpl(topicParts[0], topicParts[1], topicParts[2]);
		try {
			southboundService.processPayload(deviceModel.getManufacturer(), deviceModel.getModelId(),
					deviceModel.getVersion(), mqttData.getMqttMessage());
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			loggerService.log(deviceModel, exceptionStackToString(e));
		}
	}
}
