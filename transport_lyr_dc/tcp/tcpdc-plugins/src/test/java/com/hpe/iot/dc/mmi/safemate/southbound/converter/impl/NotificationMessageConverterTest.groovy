package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.NOTIFICATION_MESSAGE_HEX
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload

import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.NotificationMessageConverter
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerNotification
import com.hpe.iot.dc.mmi.safemate.TrackerNotification.NotificationType
import com.hpe.iot.dc.model.DeviceInfo;

import static org.junit.Assert.assertEquals;

/**
 * @author sveera
 *
 */
public class NotificationMessageConverterTest {

	private static final String EXPECTED_MESSAGE_TYPE = "0x4206";
	private final TrackerInfoCreator trackerInfoCreator = new TrackerInfoCreator();
	private final MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private NotificationMessageConverter regularUpdateMessageConverter;

	//@formatter:off
	/*
	 "04","0c","10", 			--date
	 "11","37","13", 			--time
	 "98","26","c9","02",	 	--latitude
	 "d0","2f","ad","10", 		--longitude
	 "0d","00",	 				--speed
	 "01","07",					--direction
	 "67",						--flag
	 "00","00", "00", "00",		--TSTATE
	 "64",						--Remaining Battery.
	 "00", "00", "00", "00", "00", "00", "00", -- Reserved for future
	 "fc","61","74","60",		-- GSM CELL CODE			
	 */
	//@formatter:on

	private static final String[] DEVICE_INFO_TEST_DATA = [
		"04",
		"0c",
		"10",
		"11",
		"37",
		"13",
		"98",
		"26",
		"c9",
		"02",
		"d0",
		"2f",
		"ad",
		"10",
		"0d",
		"00",
		"01",
		"07",
		"67",
		"00",
		"00",
		"00",
		"00",
		"64",
		"00",
		"00",
		"00",
		"00",
		"00",
		"00",
		"00",
		"fc",
		"61",
		"74",
		"60" ] as String[];

	@Before
	public void setUp() throws Exception {
		regularUpdateMessageConverter = new NotificationMessageConverter(mmicrcAlgorithm, trackerInfoCreator);
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected Message Type and Actual Message Type are not same", EXPECTED_MESSAGE_TYPE,
				regularUpdateMessageConverter.getMessageType());
	}

	@Test
	public void testCreateModel() {
		TrackerNotification trackerNotification = getExpectedTrackerNotification();
		DeviceInfo deviceInfo = regularUpdateMessageConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(NOTIFICATION_MESSAGE_HEX, getClass()));
		assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", MANUFACTURER,
				deviceInfo.getDevice().getManufacturer());
		assertEquals("Expected Model ID and Actual Model ID are not Same", MODEL_ID,
				deviceInfo.getDevice().getModelId());
		assertEquals("Expected device Id and Actual device Id are not same", "301071500007", deviceInfo.getDevice().getDeviceId());
		assertEquals("Expected and actual device data are not same", EXPECTED_MESSAGE_TYPE,
				deviceInfo.getMessageType());
		assertEquals("Expected Tracker Notification and Actual Tracker Notification are not same", trackerNotification,
				deviceInfo.getDeviceData().get(TrackerNotification.TRACKER_NOTIF));
	}

	private TrackerNotification getExpectedTrackerNotification() {
		List<TrackerInfo> notificationData = new ArrayList<>();
		TrackerInfo trackerInfo = trackerInfoCreator
				.constructTrackerInfo(createBinaryPayloadFromHexaPayload(DEVICE_INFO_TEST_DATA, getClass()));
		notificationData.add(trackerInfo);
		return new TrackerNotification(NotificationType.REGULAR_DATA, 1, notificationData);
	}

}
