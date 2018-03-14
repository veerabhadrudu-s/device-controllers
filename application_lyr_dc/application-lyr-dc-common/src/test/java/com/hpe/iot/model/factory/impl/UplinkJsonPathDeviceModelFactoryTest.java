package com.hpe.iot.model.factory.impl;

import static com.hpe.iot.test.constants.TestConstants.DEVICE_MODELS_FULL_PATH;
import static com.hpe.iot.test.constants.TestConstants.EMPTY_DEVICE_MODELS_FULL_PATH;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_DEV_ID_JPATH;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.REXAWARE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_DEV_ID_JPATH;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MSG_TYP_JPATH;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_DEV_ID_JPATH;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MSG_TYP_JPATH;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;

public class UplinkJsonPathDeviceModelFactoryTest {

	@Nested
	class EmptyUplinkJsonPathDeviceModelFactoryTest {
		private UplinkJsonPathDeviceModelFactory uplinkJsonPathDeviceModelFactory;

		@BeforeEach
		public void setUp() throws Exception {
			uplinkJsonPathDeviceModelFactory = new UplinkJsonPathDeviceModelFactory(EMPTY_DEVICE_MODELS_FULL_PATH);
		}

		@Test
		@DisplayName("Is UplinkJsonPathDeviceModelFactory not null")
		public final void testUplinkJsonPathDeviceModelFactory() {
			assertNotNull(uplinkJsonPathDeviceModelFactory,
					uplinkJsonPathDeviceModelFactory.getClass().getSimpleName() + " not be null");
		}

		@Test
		@DisplayName("test FindDeviceModel For Null")
		public final void testFindDeviceModelForNull() {
			assertNull(uplinkJsonPathDeviceModelFactory.findDeviceModel(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION),
					"Device Model should be null");
		}

		@Test
		@DisplayName("Is getAllDeviceModels Empty")
		public final void testGetAllDeviceModels() {
			assertTrue(uplinkJsonPathDeviceModelFactory.getAllDeviceModels().isEmpty());
		}

	}

	@Nested
	class NonEmptyUplinkJsonPathDeviceModelFactoryTest {
		private static final String SAMPLE_DEVICE_MODEL_CSV_DATA = SAMPLE + "," + SAMPLE_MODEL + "," + SAMPLE_VERSION
				+ "," + SAMPLE_DEV_ID_JPATH + "," + SAMPLE_MSG_TYP_JPATH;
		private static final String TRACKIMO_DEVICE_MODEL_CSV_DATA = TRACKIMO + "," + TRACKIMO_MODEL + ","
				+ TRACKIMO_VERSION + "," + TRACKIMO_DEV_ID_JPATH + "," + TRACKIMO_MSG_TYP_JPATH;
		private static final String REXAWARE_DEVICE_MODEL_CSV_DATA = REXAWARE + "," + REXAWARE_MODEL + ","
				+ REXAWARE_VERSION + "," + REXAWARE_DEV_ID_JPATH + ",";
		private UplinkJsonPathDeviceModelFactory uplinkJsonPathDeviceModelFactory;

		@BeforeEach
		public void setUp() throws Exception {
			uplinkJsonPathDeviceModelFactory = new UplinkJsonPathDeviceModelFactory(DEVICE_MODELS_FULL_PATH);
		}

		@Test
		@DisplayName("Is UplinkJsonPathDeviceModelFactory not null")
		public final void testUplinkJsonPathDeviceModelFactory() {
			assertNotNull(uplinkJsonPathDeviceModelFactory,
					uplinkJsonPathDeviceModelFactory.getClass().getSimpleName() + " not be null");
		}

		@CsvSource({ SAMPLE_DEVICE_MODEL_CSV_DATA, TRACKIMO_DEVICE_MODEL_CSV_DATA, REXAWARE_DEVICE_MODEL_CSV_DATA })
		@DisplayName("get JsonPathDeviceModel For  ")
		@ParameterizedTest(name = "{0} Device Model")
		public void testGetJsonPathDeviceModelForDeviceModel(String manufacturer, String modelId, String version,
				String deviceIdJsonPath, String messageTypeJsonPath) {
			DeviceModel expectedDeviceMetaModel = getExpectedJsonPathDeviceMetaModel(
					new DeviceModelImpl(manufacturer, modelId, version), deviceIdJsonPath, messageTypeJsonPath);
			DeviceModel actualDeviceMetaModel = uplinkJsonPathDeviceModelFactory.findDeviceModel(manufacturer, modelId,
					version);
			assertEquals(expectedDeviceMetaModel, actualDeviceMetaModel,
					"Expected and Actual DeviceMetaModel's are not same ");
		}

		@Test
		@DisplayName("getAllDeviceModels has all Models")
		public void testGetAllDeviceModels() {
			assertEquals(3, uplinkJsonPathDeviceModelFactory.getAllDeviceModels().size(),
					"Expected and actual device model sizes are not equal");
		}

		private DeviceModel getExpectedJsonPathDeviceMetaModel(DeviceModel deviceModel, String deviceIdJsonPath,
				String messageTypeJsonPath) {
			return new JsonPathDeviceMetaModel(deviceModel.getManufacturer(), deviceModel.getModelId(),
					deviceModel.getVersion(), deviceIdJsonPath, messageTypeJsonPath);
		}

	}

}
