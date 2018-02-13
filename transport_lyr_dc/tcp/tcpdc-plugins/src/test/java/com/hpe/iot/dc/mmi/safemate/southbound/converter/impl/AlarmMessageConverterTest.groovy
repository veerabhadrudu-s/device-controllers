package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.OFF
import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.ON
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.ALARM_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.handson.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.AlarmMessageConverter
import com.hpe.iot.dc.mmi.safemate.GPSInfo
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerNotification
import com.hpe.iot.dc.mmi.safemate.TrackerStatus
import com.hpe.iot.dc.mmi.safemate.TrackerNotification.NotificationType
import com.hpe.iot.dc.model.DeviceInfo

/**
 * @author sveera
 *
 */
public class AlarmMessageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4203";
	private AlarmMessageConverter alarmMessageConverter;

	@BeforeEach
	public void setUp() throws Exception {
		alarmMessageConverter = new AlarmMessageConverter(new MMICRCAlgorithm(), new TrackerInfoCreator());
	}

	@Test
	public void testGetMessageType() {
		assertEquals( EXPECTED_MESSAGE_TYPE,alarmMessageConverter.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}

	@Test
	public void testCreateModel() {
		DeviceInfo dataModel = alarmMessageConverter.createModel(new MMIServerSocketToDeviceModel(),
				createBinaryPayloadFromHexaPayload(ALARM_MESSAGE_HEX, IPConnectMessageConverterTest.class));
		assertEquals(MANUFACTURER,dataModel.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(MODEL_ID,dataModel.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
		assertEquals("301071500007", dataModel.getDevice().getDeviceId(),
				"Expected and actual device Id's are not same");
		TrackerNotification trackerNotification = createExpectedData();
		assertEquals(trackerNotification,dataModel.getDeviceData().get(TrackerNotification.TRACKER_NOTIF),
				"Expected and actual device data are not same");
		assertEquals(EXPECTED_MESSAGE_TYPE, dataModel.getMessageType(),
				"Expected and actual device data are not same");
	}

	private TrackerNotification createExpectedData() {
		List<TrackerInfo> trackerInfos = new ArrayList<>();
		trackerInfos.add(new TrackerInfo(new GPSInfo("6-12-2016", "9:34:27 GMT", 46788174/3600000, 279677502/3600000, 0, 0), 100,
				new TrackerStatus(ON, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF)));
		return new TrackerNotification(NotificationType.ALERT, 1, trackerInfos);
	}
}
