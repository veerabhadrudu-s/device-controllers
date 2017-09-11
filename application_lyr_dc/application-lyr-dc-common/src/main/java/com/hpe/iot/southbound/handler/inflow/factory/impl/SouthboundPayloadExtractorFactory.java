/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.factory.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.model.DeviceModel;
import com.hpe.iot.model.DeviceModelImpl;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.PayloadExtractorFactory;

/**
 * @author sveera
 *
 */
public class SouthboundPayloadExtractorFactory implements PayloadExtractorFactory {

	private Map<DeviceModel, DeviceIdExtractor> deviceIdExtractors = new ConcurrentHashMap<>();
	private Map<DeviceModel, MessageTypeExtractor> messageTypeExtractors = new ConcurrentHashMap<>();
	private Map<DeviceModel, PayloadDecipher> payloadDeciphers = new ConcurrentHashMap<>();
	private Map<DeviceModel, UplinkPayloadProcessor> uplinkPayloadProcessors = new ConcurrentHashMap<>();

	public void addDeviceIdExtractor(String manufacturer, String modelId, DeviceIdExtractor deviceIdExtractor) {
		deviceIdExtractors.put(new DeviceModelImpl(manufacturer, modelId), deviceIdExtractor);
	}

	public void addMessageTypeExtractor(String manufacturer, String modelId,
			MessageTypeExtractor messageTypeExtractor) {
		messageTypeExtractors.put(new DeviceModelImpl(manufacturer, modelId), messageTypeExtractor);
	}

	public void addPayloadDecipher(String manufacturer, String modelId, PayloadDecipher payloadDecipher) {
		payloadDeciphers.put(new DeviceModelImpl(manufacturer, modelId), payloadDecipher);
	}

	public void addUplinkPayloadProcessor(String manufacturer, String modelId,
			UplinkPayloadProcessor uplinkPayloadProcessor) {
		uplinkPayloadProcessors.put(new DeviceModelImpl(manufacturer, modelId), uplinkPayloadProcessor);
	}

	@Override
	public DeviceIdExtractor getDeviceIdExtractor(String manufacturer, String modelId) {
		return deviceIdExtractors.get(new DeviceModelImpl(manufacturer, modelId));
	}

	@Override
	public MessageTypeExtractor getMessageTypeExtractor(String manufacturer, String modelId) {
		return messageTypeExtractors.get(new DeviceModelImpl(manufacturer, modelId));
	}

	@Override
	public PayloadDecipher getPayloadDecipher(String manufacturer, String modelId) {
		return payloadDeciphers.get(new DeviceModelImpl(manufacturer, modelId));
	}

	@Override
	public UplinkPayloadProcessor getUplinkPayloadProcessor(String manufacturer, String modelId) {
		return uplinkPayloadProcessors.get(new DeviceModelImpl(manufacturer, modelId));
	}

	@Override
	public String toString() {
		return "PayloadExtractorFactoryImpl [deviceIdExtractors=" + deviceIdExtractors + ", messageTypeExtractors="
				+ messageTypeExtractors + ", payloadDeciphers=" + payloadDeciphers + "]";
	}

}
