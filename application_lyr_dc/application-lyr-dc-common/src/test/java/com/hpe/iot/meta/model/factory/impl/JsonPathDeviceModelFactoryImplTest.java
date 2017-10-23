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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setUp() throws Exception {
		deviceMetaModelFactoryImpl = new UplinkJsonPathDeviceModelFactory(TestConstants.FULL_PATH);
	}

	@Test
	public void testGetDeviceMetaModelForTrackimo() {
		DeviceModel expectedDeviceMetaModel = getExpectedTrackimoDeviceMetaModel();
		DeviceModel actualDeviceMetaModel = deviceMetaModelFactoryImpl.findDeviceModel(TRACKIMO, TRACKIMO_MODEL,
				TRACKIMO_VERSION);
		Assert.assertEquals("Expected and Actual DeviceMetaModel's are not same ", expectedDeviceMetaModel,
				actualDeviceMetaModel);
	}

	@Test
	public void testGetDeviceMetaModelForSample() {
		DeviceModel expectedDeviceMetaModel = getExpectedSampleDeviceMetaModel();
		DeviceModel actualDeviceMetaModel = deviceMetaModelFactoryImpl.findDeviceModel(SAMPLE, SAMPLE_MODEL,
				SAMPLE_VERSION);
		Assert.assertEquals("Expected and Actual DeviceMetaModel's are not same ", expectedDeviceMetaModel,
				actualDeviceMetaModel);
	}

	private DeviceModel getExpectedSampleDeviceMetaModel() {
		return new JsonPathDeviceMetaModel(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION, "$.sample_id", "$.message_type");
	}

	private DeviceModel getExpectedTrackimoDeviceMetaModel() {
		return new JsonPathDeviceMetaModel(TRACKIMO, TRACKIMO_MODEL, TRACKIMO_VERSION, "$.device_id", "$.alarm_type");
	}

}
