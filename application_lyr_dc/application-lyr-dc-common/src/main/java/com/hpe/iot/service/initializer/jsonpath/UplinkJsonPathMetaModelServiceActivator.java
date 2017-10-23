/**
 * 
 */
package com.hpe.iot.service.initializer.jsonpath;

import java.util.List;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.service.initializer.ServiceActivator;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultPayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.impl.JsonPathDeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.impl.JsonPathMessageTypeExtractor;

/**
 * @author sveera
 *
 */
public class UplinkJsonPathMetaModelServiceActivator implements ServiceActivator {

	private final DeviceModelFactory deviceModelFactory;
	private final SouthboundPayloadExtractorFactory payloadExtractorFactoryImpl;
	private final UplinkPayloadProcessor defaultUplinkPayloadProcessor;

	public UplinkJsonPathMetaModelServiceActivator(DeviceModelFactory deviceModelFactory,
			SouthboundPayloadExtractorFactory payloadExtractorFactoryImpl,
			UplinkPayloadProcessor defaultUplinkPayloadProcessor) {
		super();
		this.deviceModelFactory = deviceModelFactory;
		this.payloadExtractorFactoryImpl = payloadExtractorFactoryImpl;
		this.defaultUplinkPayloadProcessor = defaultUplinkPayloadProcessor;
	}

	@Override
	public void startAllServices() {
		fillExtractorsForJsonPathDeviceDataModels();
	}

	@Override
	public void stopAllServices() {

	}

	private void fillExtractorsForJsonPathDeviceDataModels() {
		List<DeviceModel> jsonPathDeviceModels = deviceModelFactory.getAllDeviceModels();
		JsonPathDeviceIdExtractor jsonPathDeviceIdExtractor = new JsonPathDeviceIdExtractor();
		JsonPathMessageTypeExtractor jsonPathMessageTypeExtractor = new JsonPathMessageTypeExtractor();
		PayloadDecipher payloadDecipher = new DefaultPayloadDecipher();
		for (DeviceModel deviceModel : jsonPathDeviceModels) {
			payloadExtractorFactoryImpl.addDeviceIdExtractor(deviceModel.getManufacturer(), deviceModel.getModelId(),
					jsonPathDeviceIdExtractor);
			payloadExtractorFactoryImpl.addMessageTypeExtractor(deviceModel.getManufacturer(), deviceModel.getModelId(),
					jsonPathMessageTypeExtractor);
			payloadExtractorFactoryImpl.addPayloadDecipher(deviceModel.getManufacturer(), deviceModel.getModelId(),
					payloadDecipher);
			payloadExtractorFactoryImpl.addUplinkPayloadProcessor(deviceModel.getManufacturer(),
					deviceModel.getModelId(), defaultUplinkPayloadProcessor);
		}
	}

}
