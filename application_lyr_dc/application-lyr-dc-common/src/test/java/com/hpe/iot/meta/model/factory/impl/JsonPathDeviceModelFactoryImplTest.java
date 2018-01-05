/**
 * 
 */
package com.hpe.iot.meta.model.factory.impl;

import static com.hpe.iot.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_MODEL;
import static com.hpe.iot.test.constants.TestConstants.TRACKIMO_VERSION;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.impl.UplinkJsonPathDeviceModelFactory;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;
import com.hpe.iot.test.constants.TestConstants;

/**
 * @author sveera
 *
 */
public class JsonPathDeviceModelFactoryImplTest {

	private UplinkJsonPathDeviceModelFactory deviceMetaModelFactoryImpl;

	@BeforeEach
	public void setUp() throws Exception {
		deviceMetaModelFactoryImpl = new UplinkJsonPathDeviceModelFactory(TestConstants.FULL_PATH);
	}

	@Test
	@DisplayName("Get DeviceMetaModel For Trackimo")
	public void testGetDeviceMetaModelForTrackimo() {
		DeviceModel expectedDeviceMetaModel = getExpectedTrackimoDeviceMetaModel();
		DeviceModel actualDeviceMetaModel = deviceMetaModelFactoryImpl.findDeviceModel(TRACKIMO, TRACKIMO_MODEL,
				TRACKIMO_VERSION);
		Assertions.assertEquals(expectedDeviceMetaModel, actualDeviceMetaModel,
				"Expected and Actual DeviceMetaModel's are not same ");
	}

	@Test
	@DisplayName("Get DeviceMetaModel For Sample")
	public void testGetDeviceMetaModelForSample() {
		DeviceModel expectedDeviceMetaModel = getExpectedSampleDeviceMetaModel();
		DeviceModel actualDeviceMetaModel = deviceMetaModelFactoryImpl.findDeviceModel(SAMPLE, SAMPLE_MODEL,
				SAMPLE_VERSION);
		Assertions.assertEquals(expectedDeviceMetaModel, actualDeviceMetaModel,
				"Expected and Actual DeviceMetaModel's are not same ");
	}

	private DeviceModel getExpectedSampleDeviceMetaModel() {
		return new JsonPathDeviceMetaModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, "$.sample_id", "$.message_type");
	}

	private DeviceModel getExpectedTrackimoDeviceMetaModel() {
		return new JsonPathDeviceMetaModel(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, "$.device_id", "$.alarm_type");
	}

}
