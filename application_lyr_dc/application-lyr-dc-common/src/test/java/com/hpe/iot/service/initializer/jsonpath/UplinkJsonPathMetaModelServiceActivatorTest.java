package com.hpe.iot.service.initializer.jsonpath;

import static com.hpe.iot.test.constants.TestConstants.DEVICE_MODELS_FULL_PATH;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_VERSION;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.impl.UplinkJsonPathDeviceModelFactory;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;
import com.hpe.iot.southbound.handler.inflow.impl.DefaultUplinkPayloadProcessor;

/**
 * @author sveera
 *
 */
public class UplinkJsonPathMetaModelServiceActivatorTest
		implements IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> {
	private final SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory = new SouthboundPayloadExtractorFactory();
	private final UplinkJsonPathDeviceModelFactory uplinkJsonPathDeviceModelFactory = new UplinkJsonPathDeviceModelFactory(
			DEVICE_MODELS_FULL_PATH);
	private UplinkJsonPathMetaModelServiceActivator uplinkJsonPathMetaModelServiceActivator;

	@BeforeEach
	void setUp() throws Exception {
		UplinkPayloadProcessor defaultUplinkPayloadProcessor = new DefaultUplinkPayloadProcessor(this);
		uplinkJsonPathMetaModelServiceActivator = new UplinkJsonPathMetaModelServiceActivator(
				uplinkJsonPathDeviceModelFactory, southboundPayloadExtractorFactory, defaultUplinkPayloadProcessor);
	}

	@Test
	@DisplayName("Is UplinkJsonPathMetaModelServiceActivator not null")
	final public void testUplinkJsonPathMetaModelServiceActivator() {
		assertNotNull(uplinkJsonPathMetaModelServiceActivator,
				UplinkJsonPathMetaModelServiceActivator.class.getSimpleName() + " cannot be null");
	}

	@Test
	@DisplayName("Is all groovy plugin scripts services started")
	final public void testStartAllServices() {
		uplinkJsonPathMetaModelServiceActivator.startAllServices();
		assertPluginScriptHandlerClasses(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
		assertPluginScriptHandlerClasses(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION);
		assertPluginScriptHandlerClasses(REXAWARE, REXAWARE_MODEL, REXAWARE_VERSION);
	}

	private void assertPluginScriptHandlerClasses(String manufacturer, String modelId, String version) {
		assertNotNull(uplinkJsonPathDeviceModelFactory.findDeviceModel(manufacturer, modelId, version),
				DeviceModel.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getPayloadDecipher(manufacturer, modelId, version),
				PayloadDecipher.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getDeviceIdExtractor(manufacturer, modelId, version),
				DeviceIdExtractor.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getMessageTypeExtractor(manufacturer, modelId, version),
				MessageTypeExtractor.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getUplinkPayloadProcessor(manufacturer, modelId, version),
				UplinkPayloadProcessor.class.getSimpleName() + " cannot be null");
	}

	@Override
	public DeviceDataDeliveryStatus receiveDataFromDevice(DeviceInfo request, String containerName) {
		return null;
	}

}
