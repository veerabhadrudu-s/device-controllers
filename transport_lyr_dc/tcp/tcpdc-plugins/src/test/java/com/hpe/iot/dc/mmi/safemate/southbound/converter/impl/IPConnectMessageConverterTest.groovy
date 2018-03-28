package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.TrackerNotification.TRACKER_NOTIF
import static com.hpe.iot.dc.mmi.safemate.TrackerNotification.NotificationType.IPCONNECT
import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.OFF;
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.IPCONNECT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.GPSInfo
import com.hpe.iot.dc.mmi.safemate.IPConnectMessageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerNotification
import com.hpe.iot.dc.mmi.safemate.TrackerStatus
import com.hpe.iot.dc.model.DeviceInfo;


/**
 * @author sveera
 *
 */
public class IPConnectMessageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4001";

	private IPConnectMessageConverter ipAddressMetaModelConverter;

	@BeforeEach
	public void setUpBeforeClass() throws Exception {
		ipAddressMetaModelConverter = new IPConnectMessageConverter(new MMICRCAlgorithm(), new TrackerInfoCreator());
	}

	@Test
	public void testGetOperator() {
		assertEquals(EXPECTED_MESSAGE_TYPE,ipAddressMetaModelConverter.getMessageType(),
				"Expected and actual Operators are not same");
	}

	@Test
	public void testCreateModeForIPConnectMessageType() {
		DeviceInfo dataModel = ipAddressMetaModelConverter
				.createModel(new MMIServerSocketToDeviceModel(),createDecimalPayloadFromHexaPayload(IPCONNECT_DATA_MESSAGE_HEX, this.getClass()));
		assertEquals(MANUFACTURER,dataModel.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(MODEL_ID,dataModel.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
		assertEquals("301071500007", dataModel.getDevice().getDeviceId(),
				"Expected and actual device Id's are not same");
		TrackerNotification trackerNotification = createExpectedData();
		assertEquals(trackerNotification,dataModel.getDeviceData().get(TRACKER_NOTIF),
				"Expected and actual device data are not same");
		assertEquals(EXPECTED_MESSAGE_TYPE, dataModel.getMessageType(),
				"Expected and actual device data are not same");
	}

	private TrackerNotification createExpectedData() {
		List<TrackerInfo> trackerInfos=new ArrayList<>();
		trackerInfos.add(new TrackerInfo(new GPSInfo("12-2-2001", "12:21:23 GMT", 4228040996/3600000, 1092906/3600000, 377.82, 768), 100,
				new TrackerStatus(OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF)));
		return new TrackerNotification(IPCONNECT, 1, trackerInfos);
	}
}
