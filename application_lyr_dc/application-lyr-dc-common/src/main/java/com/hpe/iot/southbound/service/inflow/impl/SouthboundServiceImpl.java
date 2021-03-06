/**
 * 
 */
package com.hpe.iot.southbound.service.inflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.PayloadExtractorFactory;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
public class SouthboundServiceImpl implements SouthboundService {

	private final DeviceModelFactory deviceModelFactory;
	private final PayloadExtractorFactory payloadExtractorFactory;

	public SouthboundServiceImpl(DeviceModelFactory deviceModelFactory,
			PayloadExtractorFactory payloadExtractorFactory) {
		super();
		this.deviceModelFactory = deviceModelFactory;
		this.payloadExtractorFactory = payloadExtractorFactory;
	}

	@Override
	public void processPayload(String manufacturer, String modelId, String version, byte[] payload) {
		DeviceModel deviceModel = deviceModelFactory.findDeviceModel(manufacturer, modelId, version);
		if (deviceModel == null)
			throw new DeviceModelNotSuported(manufacturer, modelId, version);
		DeviceInfo deviceInfo = constructDeviceInfo(deviceModel, payload);
		UplinkPayloadProcessor uplinkPayloadProcessor = payloadExtractorFactory.getUplinkPayloadProcessor(manufacturer,
				modelId, version);
		uplinkPayloadProcessor.processPayload(deviceInfo);
	}

	private DeviceInfo constructDeviceInfo(DeviceModel deviceModel, byte[] payload) {
		JsonObject deciperedPayload = deciperPayload(deviceModel, payload);
		String deviceId = findDeviceId(deviceModel, deciperedPayload);
		String messageType = findMessageType(deviceModel, deciperedPayload);
		Device device = new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), deviceId);
		return new DeviceInfo(device, messageType, deciperedPayload);
	}

	private JsonObject deciperPayload(DeviceModel deviceModel, byte[] payload) {
		PayloadDecipher payloadDecipher = payloadExtractorFactory.getPayloadDecipher(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion());
		return payloadDecipher.decipherPayload(deviceModel, payload);
	}

	private String findDeviceId(DeviceModel deviceModel, JsonObject payload) {
		DeviceIdExtractor deviceIdExtractor = payloadExtractorFactory.getDeviceIdExtractor(
				deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion());
		return deviceIdExtractor.extractDeviceId(deviceModel, payload);
	}

	private String findMessageType(DeviceModel deviceModel, JsonObject payload) {
		MessageTypeExtractor messageTypeExtractor = payloadExtractorFactory.getMessageTypeExtractor(
				deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion());
		return messageTypeExtractor.extractMessageType(deviceModel, payload);
	}

	class DeviceModelNotSuported extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public DeviceModelNotSuported(String manufacturer, String modelId, String version) {
			super("Device Model with manufacturer: " + manufacturer + " with modelId : " + modelId + " with version :"
					+ version + " not supported.");
		}

	}

}
