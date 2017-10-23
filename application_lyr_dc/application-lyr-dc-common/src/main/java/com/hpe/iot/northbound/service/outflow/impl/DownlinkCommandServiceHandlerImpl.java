/**
 * 
 */
package com.hpe.iot.northbound.service.outflow.impl;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.PayloadExtractorFactory;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandServiceHandler;

/**
 * @author sveera
 *
 */
public class DownlinkCommandServiceHandlerImpl implements DownlinkCommandServiceHandler {

	private final DeviceModelFactory deviceModelFactory;
	private final PayloadExtractorFactory payloadExtractorFactory;

	public DownlinkCommandServiceHandlerImpl(DeviceModelFactory deviceModelFactory,
			PayloadExtractorFactory payloadExtractorFactory) {
		super();
		this.deviceModelFactory = deviceModelFactory;
		this.payloadExtractorFactory = payloadExtractorFactory;
	}

	@Override
	public void processPayload(DeviceInfo deviceInfo) {
		Device device = deviceInfo.getDevice();
		DeviceModel deviceModel = deviceModelFactory.findDeviceModel(device.getManufacturer(), device.getModelId());
		DownlinkPayloadProcessor downlinkPayloadProcessor = payloadExtractorFactory
				.getDownlinkPayloadProcessor(device.getManufacturer(), device.getModelId());
		if (deviceModel == null)
			throw new DeviceModelNotSuported(device.getManufacturer(), device.getModelId());
		if (downlinkPayloadProcessor == null)
			throw new DownlinkFlowNotSupported(device.getManufacturer(), device.getModelId());
		PayloadCipher payloadCipher = payloadExtractorFactory.getPayloadCipher(device.getManufacturer(),
				device.getModelId());
		JsonObject cipheredPayload = payloadCipher.cipherPayload(deviceModel, deviceInfo.getPayload());
		DeviceInfo downlinkDeviceInfo = new DeviceInfo(device, deviceInfo.getMessageType(), cipheredPayload);
		downlinkPayloadProcessor.processPayload(deviceModel, downlinkDeviceInfo);

	}

	class DeviceModelNotSuported extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DeviceModelNotSuported(String manufacturer, String modelId) {
			super("Device Model with manufacturer: " + manufacturer + " with modelId : " + modelId + " not supported.");
		}

	}

	class DownlinkFlowNotSupported extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DownlinkFlowNotSupported(String manufacturer, String modelId) {
			super("Downlink Flow Not Supported for manufacturer: " + manufacturer + " with modelId : " + modelId);
		}

	}

}
