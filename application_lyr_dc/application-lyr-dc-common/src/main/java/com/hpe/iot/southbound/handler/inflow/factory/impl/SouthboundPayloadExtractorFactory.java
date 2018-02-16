/**
 * 
 */
package com.hpe.iot.southbound.handler.inflow.factory.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
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

	public void addDeviceIdExtractor(String manufacturer, String modelId, String version,
			DeviceIdExtractor deviceIdExtractor) {
		deviceIdExtractors.put(new DeviceModelImpl(manufacturer, modelId, version), deviceIdExtractor);
	}

	public void addMessageTypeExtractor(String manufacturer, String modelId, String version,
			MessageTypeExtractor messageTypeExtractor) {
		messageTypeExtractors.put(new DeviceModelImpl(manufacturer, modelId, version), messageTypeExtractor);
	}

	public void addPayloadDecipher(String manufacturer, String modelId, String version,
			PayloadDecipher payloadDecipher) {
		payloadDeciphers.put(new DeviceModelImpl(manufacturer, modelId, version), payloadDecipher);
	}

	public void addUplinkPayloadProcessor(String manufacturer, String modelId, String version,
			UplinkPayloadProcessor uplinkPayloadProcessor) {
		uplinkPayloadProcessors.put(new DeviceModelImpl(manufacturer, modelId, version), uplinkPayloadProcessor);
	}

	public void removeDeviceIdExtractor(String manufacturer, String modelId, String version) {
		deviceIdExtractors.remove(new DeviceModelImpl(manufacturer, modelId, version));
	}

	public void removeMessageTypeExtractor(String manufacturer, String modelId, String version) {
		messageTypeExtractors.remove(new DeviceModelImpl(manufacturer, modelId, version));
	}

	public void removePayloadDecipher(String manufacturer, String modelId, String version) {
		payloadDeciphers.remove(new DeviceModelImpl(manufacturer, modelId, version));
	}

	public void removeUplinkPayloadProcessor(String manufacturer, String modelId, String version) {
		uplinkPayloadProcessors.remove(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public DeviceIdExtractor getDeviceIdExtractor(String manufacturer, String modelId, String version) {
		return deviceIdExtractors.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public MessageTypeExtractor getMessageTypeExtractor(String manufacturer, String modelId, String version) {
		return messageTypeExtractors.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public PayloadDecipher getPayloadDecipher(String manufacturer, String modelId, String version) {
		return payloadDeciphers.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public UplinkPayloadProcessor getUplinkPayloadProcessor(String manufacturer, String modelId, String version) {
		return uplinkPayloadProcessors.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public String toString() {
		return "PayloadExtractorFactoryImpl [deviceIdExtractors=" + deviceIdExtractors + ", messageTypeExtractors="
				+ messageTypeExtractors + ", payloadDeciphers=" + payloadDeciphers + "]";
	}

}
