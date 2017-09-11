package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.OFF
import static com.hpe.iot.dc.mmi.safemate.TrackerStatus.AlarmStatus.ON
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.ALARM_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload

import org.junit.Before;
import org.junit.Test;

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

import static org.junit.Assert.assertEquals;

/**
 * @author sveera
 *
 */
public class AlarmMessageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4203";
	private AlarmMessageConverter alarmMessageConverter;

	@Before
	public void setUp() throws Exception {
		alarmMessageConverter = new AlarmMessageConverter(new MMICRCAlgorithm(), new TrackerInfoCreator());
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected Message Type and Actual Message Type are not same.", EXPECTED_MESSAGE_TYPE,
				alarmMessageConverter.getMessageType());
	}

	@Test
	public void testCreateModel() {
		DeviceInfo dataModel = alarmMessageConverter.createModel(new MMIServerSocketToDeviceModel(),
				createBinaryPayloadFromHexaPayload(ALARM_MESSAGE_HEX, IPConnectMessageConverterTest.class));
		assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", MANUFACTURER,
				dataModel.getDevice().getManufacturer());
		assertEquals("Expected Model ID and Actual Model ID are not Same", MODEL_ID,
				dataModel.getDevice().getModelId());
		assertEquals("Expected and actual device Id's are not same", "301071500007", dataModel.getDevice().getDeviceId());
		TrackerNotification trackerNotification = createExpectedData();
		assertEquals("Expected and actual device data are not same", trackerNotification,
				dataModel.getDeviceData().get(TrackerNotification.TRACKER_NOTIF));
		assertEquals("Expected and actual device data are not same", EXPECTED_MESSAGE_TYPE, dataModel.getMessageType());
	}

	private TrackerNotification createExpectedData() {
		List<TrackerInfo> trackerInfos = new ArrayList<>();
		trackerInfos.add(new TrackerInfo(new GPSInfo("6-12-2016", "9:34:27 GMT", 46788174/3600000, 279677502/3600000, 0, 0), 100,
				new TrackerStatus(ON, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF, OFF)));
		return new TrackerNotification(NotificationType.ALERT, 1, trackerInfos);
	}
}
