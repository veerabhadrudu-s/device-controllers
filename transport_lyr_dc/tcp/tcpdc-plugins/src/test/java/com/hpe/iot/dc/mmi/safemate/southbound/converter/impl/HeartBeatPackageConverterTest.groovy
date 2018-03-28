package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.HEART_BEAT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.VERSION
import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.HeartBeatPackageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class HeartBeatPackageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4003";

	private HeartBeatPackageConverter heartBeatPackageConverter;

	@BeforeEach
	public void setUp() throws Exception {
		heartBeatPackageConverter = new HeartBeatPackageConverter(new MMICRCAlgorithm());
	}

	@Test
	public void testCreateModelForHeartBeatType() {
		DeviceInfo expectedDeviceInfo = createExpectedDeviceInfo();
		assertEquals(MANUFACTURER,expectedDeviceInfo.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(MODEL_ID,expectedDeviceInfo.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
		assertEquals(expectedDeviceInfo,
				heartBeatPackageConverter.createModel(new MMIServerSocketToDeviceModel(),
				createDecimalPayloadFromHexaPayload(HEART_BEAT_DATA_MESSAGE_HEX, getClass()))
				,"Expected Device Info and Actual Device Info are not same");
	}

	@Test
	public void testGetMessageType() {
		assertEquals(EXPECTED_MESSAGE_TYPE,heartBeatPackageConverter.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}

	private DeviceInfo createExpectedDeviceInfo() {
		return new DeviceInfo(new DeviceImpl(MANUFACTURER,MODEL_ID,VERSION,"301071500007"), EXPECTED_MESSAGE_TYPE,
				createDecimalPayloadFromHexaPayload(HEART_BEAT_DATA_MESSAGE_HEX, getClass()));
	}
}
