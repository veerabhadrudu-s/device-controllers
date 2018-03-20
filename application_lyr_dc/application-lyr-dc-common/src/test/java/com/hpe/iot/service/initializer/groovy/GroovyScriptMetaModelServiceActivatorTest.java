package com.hpe.iot.service.initializer.groovy;

import static com.hpe.iot.test.constants.TestConstants.EMPTY_DEVICE_MODELS_FULL_PATH;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_1;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_2;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.handson.logger.service.DeploymentLoggerService;
import com.handson.logger.service.impl.Slf4jDeploymentLoggerService;
import com.hpe.iot.bean.pool.ServerBeanPool;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.model.factory.impl.GroovyAndUplinkJsonPathDeviceModelFactory;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.service.initializer.groovy.file.impl.GroovyScriptFileToDeviceModelHolderImpl;
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
public class GroovyScriptMetaModelServiceActivatorTest
		implements ServerBeanPool, IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> {

	private final List<String> directoriesForGroovyScripts = new ArrayList<>();
	private final GroovyAndUplinkJsonPathDeviceModelFactory groovyAndUplinkJsonPathDeviceModelFactory = new GroovyAndUplinkJsonPathDeviceModelFactory(
			EMPTY_DEVICE_MODELS_FULL_PATH);
	private final SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory = new SouthboundPayloadExtractorFactory();
	private final NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory = new NorthboundPayloadExtractorFactory();
	private GroovyScriptMetaModelServiceActivator groovyScriptMetaModelServiceActivator;

	@BeforeEach
	void setUp() throws Exception {
		final GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolderImpl = new GroovyScriptFileToDeviceModelHolderImpl();
		final GroovyScriptModelCreator groovyScriptModelCreator = new GroovyScriptModelCreator(this);
		final DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder = new DefaultPayloadExtractorFactoryComponentHolder(
				new DefaultUplinkPayloadProcessor(this));
		final DeploymentLoggerService deploymentLoggerService = new Slf4jDeploymentLoggerService();
		groovyScriptMetaModelServiceActivator = new GroovyScriptMetaModelServiceActivator(directoriesForGroovyScripts,
				groovyScriptFileToDeviceModelHolderImpl, southboundPayloadExtractorFactory,
				northboundPayloadExtractorFactory, groovyAndUplinkJsonPathDeviceModelFactory, groovyScriptModelCreator,
				defaultPayloadExtractorFactoryComponentHolder, deploymentLoggerService);
	}

	@Test
	@DisplayName("Is groovyScriptMetaModelServiceActivator not null")
	final void testGroovyScriptMetaModelServiceActivator() {
		assertNotNull(groovyScriptMetaModelServiceActivator,
				GroovyScriptMetaModelServiceActivator.class.getName() + " cannot be null");
	}

	@Test
	@DisplayName("Is all groovy plugin scripts services started")
	final void testStartAllServices() {
		configurePluginsAndStartServices();
		assertEquals(2, groovyAndUplinkJsonPathDeviceModelFactory.getAllDeviceModels().size(),
				"Expected and actual device models are not equals");
		assertPluginScriptHandlerClasses(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
		assertPluginScriptHandlerClasses(SAMPLE_1, SAMPLE_MODEL, SAMPLE_VERSION);
	}

	@Nested
	public class StartPluginScriptServiceTest {

		@BeforeEach
		public void configurePluginsAndStartServices() {
			GroovyScriptMetaModelServiceActivatorTest.this.configurePluginsAndStartServices();
		}

		@Test
		@DisplayName("test Start Plugin Script Service On The Fly")
		final void testStartPluginScriptServiceOnTheFly() {
			groovyScriptMetaModelServiceActivator
					.startService("src/test/java/com/hpe/iot/dc/groovyscript/sample_2/model/sample_2.groovy");
			assertPluginScriptHandlerClasses(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
			assertPluginScriptHandlerClasses(SAMPLE_1, SAMPLE_MODEL, SAMPLE_VERSION);
			assertPluginScriptHandlerClasses(SAMPLE_2, SAMPLE_MODEL, SAMPLE_VERSION);
		}

	}

	@Nested
	public class StopPluginScriptServiceTest {

		@BeforeEach
		public void configurePluginsAndStartServices() {
			GroovyScriptMetaModelServiceActivatorTest.this.configurePluginsAndStartServices();
		}

		@Test
		@DisplayName("test Stop One Plugin Script Service On The Fly")
		final void testStopOnePluginScriptServiceOnTheFly() {
			groovyScriptMetaModelServiceActivator
					.stopService("src/test/java/com/hpe/iot/dc/groovyscript/sample/model/sample.groovy");
			assertRemovedPluginScriptHandlerClasses(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
			assertPluginScriptHandlerClasses(SAMPLE_1, SAMPLE_MODEL, SAMPLE_VERSION);
		}

		@Test
		@DisplayName("test Stop All Plugin Script Service On The Fly")
		final void testStopAllPluginScriptServicesOnTheFly() {
			groovyScriptMetaModelServiceActivator
					.stopService("src/test/java/com/hpe/iot/dc/groovyscript/sample/model/sample.groovy");
			groovyScriptMetaModelServiceActivator
					.stopService("src/test/java/com/hpe/iot/dc/groovyscript/sample_1/model/sample_1.groovy");
			assertRemovedPluginScriptHandlerClasses(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION);
			assertRemovedPluginScriptHandlerClasses(SAMPLE_1, SAMPLE_MODEL, SAMPLE_VERSION);
		}

	}

	private void configurePluginsAndStartServices() {
		directoriesForGroovyScripts.add("src/test/java/com/hpe/iot/dc/groovyscript/sample/model");
		directoriesForGroovyScripts.add("src/test/java/com/hpe/iot/dc/groovyscript/sample_1/model");
		groovyScriptMetaModelServiceActivator.startAllServices();
	}

	private void assertPluginScriptHandlerClasses(String manufacturer, String modelId, String version) {
		assertNotNull(groovyAndUplinkJsonPathDeviceModelFactory.findDeviceModel(manufacturer, modelId, version),
				DeviceModel.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getPayloadDecipher(manufacturer, modelId, version),
				PayloadDecipher.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getDeviceIdExtractor(manufacturer, modelId, version),
				DeviceIdExtractor.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getMessageTypeExtractor(manufacturer, modelId, version),
				MessageTypeExtractor.class.getSimpleName() + " cannot be null");
		assertNotNull(southboundPayloadExtractorFactory.getUplinkPayloadProcessor(manufacturer, modelId, version),
				UplinkPayloadProcessor.class.getSimpleName() + " cannot be null");
		assertNotNull(northboundPayloadExtractorFactory.getDownlinkPayloadProcessor(manufacturer, modelId, version),
				DownlinkPayloadProcessor.class.getSimpleName() + " cannot be null");
		assertNotNull(northboundPayloadExtractorFactory.getPayloadCipher(manufacturer, modelId, version),
				PayloadCipher.class.getSimpleName() + " cannot be null");
	}

	private void assertRemovedPluginScriptHandlerClasses(String manufacturer, String modelId, String version) {
		assertNull(groovyAndUplinkJsonPathDeviceModelFactory.findDeviceModel(manufacturer, modelId, version),
				DeviceModel.class.getSimpleName() + " should be null");
		assertNull(southboundPayloadExtractorFactory.getPayloadDecipher(manufacturer, modelId, version),
				PayloadDecipher.class.getSimpleName() + " should be null");
		assertNull(southboundPayloadExtractorFactory.getDeviceIdExtractor(manufacturer, modelId, version),
				DeviceIdExtractor.class.getSimpleName() + " should be null");
		assertNull(southboundPayloadExtractorFactory.getMessageTypeExtractor(manufacturer, modelId, version),
				MessageTypeExtractor.class.getSimpleName() + " should be null");
		assertNull(southboundPayloadExtractorFactory.getUplinkPayloadProcessor(manufacturer, modelId, version),
				UplinkPayloadProcessor.class.getSimpleName() + " should be null");
		assertNull(northboundPayloadExtractorFactory.getDownlinkPayloadProcessor(manufacturer, modelId, version),
				DownlinkPayloadProcessor.class.getSimpleName() + " should be null");
		assertNull(northboundPayloadExtractorFactory.getPayloadCipher(manufacturer, modelId, version),
				PayloadCipher.class.getSimpleName() + " should be null");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> classType) {
		if (IOTPublisherService.class.isAssignableFrom(classType))
			return (T) this;

		return null;
	}

	@Override
	public DeviceDataDeliveryStatus receiveDataFromDevice(DeviceInfo request, String containerName) {
		return null;
	}

}
