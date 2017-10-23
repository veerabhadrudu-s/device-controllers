package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.HEART_BEAT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.VERSION
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.mmi.safemate.HeartBeatPackageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class HeartBeatPackageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4003";

	private HeartBeatPackageConverter heartBeatPackageConverter;

	@Before
	public void setUp() throws Exception {
		heartBeatPackageConverter = new HeartBeatPackageConverter(new MMICRCAlgorithm());
	}

	@Test
	public void testCreateModelForHeartBeatType() {
		DeviceInfo expectedDeviceInfo = createExpectedDeviceInfo();
		assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", MANUFACTURER,
				expectedDeviceInfo.getDevice().getManufacturer());
		assertEquals("Expected Model ID and Actual Model ID are not Same", MODEL_ID,
				expectedDeviceInfo.getDevice().getModelId());
		assertEquals("Expected Device Info and Actual Device Info are not Same", expectedDeviceInfo,
				heartBeatPackageConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(HEART_BEAT_DATA_MESSAGE_HEX, getClass())));
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected Message Type and Actual Message Type are not same.", EXPECTED_MESSAGE_TYPE,
				heartBeatPackageConverter.getMessageType());
	}

	private DeviceInfo createExpectedDeviceInfo() {
		return new DeviceInfo(new DeviceImpl(MANUFACTURER,MODEL_ID,VERSION,"301071500007"), EXPECTED_MESSAGE_TYPE,
				createBinaryPayloadFromHexaPayload(HEART_BEAT_DATA_MESSAGE_HEX, getClass()));
	}
}
