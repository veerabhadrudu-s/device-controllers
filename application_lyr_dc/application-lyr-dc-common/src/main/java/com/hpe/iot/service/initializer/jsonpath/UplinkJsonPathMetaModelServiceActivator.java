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
	private final SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	private final UplinkPayloadProcessor defaultUplinkPayloadProcessor;

	public UplinkJsonPathMetaModelServiceActivator(DeviceModelFactory deviceModelFactory,
			SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory,
			UplinkPayloadProcessor defaultUplinkPayloadProcessor) {
		super();
		this.deviceModelFactory = deviceModelFactory;
		this.southboundPayloadExtractorFactory = southboundPayloadExtractorFactory;
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
			southboundPayloadExtractorFactory.addDeviceIdExtractor(deviceModel.getManufacturer(),
					deviceModel.getModelId(), deviceModel.getVersion(), jsonPathDeviceIdExtractor);
			southboundPayloadExtractorFactory.addMessageTypeExtractor(deviceModel.getManufacturer(),
					deviceModel.getModelId(), deviceModel.getVersion(), jsonPathMessageTypeExtractor);
			southboundPayloadExtractorFactory.addPayloadDecipher(deviceModel.getManufacturer(),
					deviceModel.getModelId(), deviceModel.getVersion(), payloadDecipher);
			southboundPayloadExtractorFactory.addUplinkPayloadProcessor(deviceModel.getManufacturer(),
					deviceModel.getModelId(), deviceModel.getVersion(), defaultUplinkPayloadProcessor);
		}
	}

}
