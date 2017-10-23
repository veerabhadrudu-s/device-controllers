/**
 * 
 */
package com.hpe.iot.northbound.handler.outflow.factory.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.PayloadExtractorFactory;

/**
 * @author sveera
 *
 */
public class NorthboundPayloadExtractorFactory implements PayloadExtractorFactory {

	private Map<DeviceModel, PayloadCipher> payloadCiphers = new ConcurrentHashMap<>();
	private Map<DeviceModel, DownlinkPayloadProcessor> downlinkPayloadProcessors = new ConcurrentHashMap<>();

	public void addPayloadCipher(String manufacturer, String modelId, String version, PayloadCipher payloadCipher) {
		payloadCiphers.put(new DeviceModelImpl(manufacturer, modelId, version), payloadCipher);
	}

	public void addDownlinkPayloadProcessor(String manufacturer, String modelId, String version,
			DownlinkPayloadProcessor downlinkPayloadProcessor) {
		downlinkPayloadProcessors.put(new DeviceModelImpl(manufacturer, modelId, version), downlinkPayloadProcessor);
	}

	@Override
	public PayloadCipher getPayloadCipher(String manufacturer, String modelId, String version) {
		return payloadCiphers.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public DownlinkPayloadProcessor getDownlinkPayloadProcessor(String manufacturer, String modelId, String version) {
		return downlinkPayloadProcessors.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public String toString() {
		return "PayloadExtractorFactoryImpl [payloadCiphers=" + payloadCiphers + ", downlinkPayloadProcessors="
				+ downlinkPayloadProcessors + "]";
	}

}
