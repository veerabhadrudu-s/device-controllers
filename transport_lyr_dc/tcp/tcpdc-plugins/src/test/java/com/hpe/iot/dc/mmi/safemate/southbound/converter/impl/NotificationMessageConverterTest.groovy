package com.hpe.iot.dc.mmi.safemate.southbound.converter.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MANUFACTURER
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.MODEL_ID
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.NOTIFICATION_MESSAGE_HEX
import static com.handson.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.NotificationMessageConverter
import com.hpe.iot.dc.mmi.safemate.TrackerInfo
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.TrackerNotification
import com.hpe.iot.dc.mmi.safemate.TrackerNotification.NotificationType
import com.hpe.iot.dc.model.DeviceInfo;


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

	@BeforeEach
	public void setUp() throws Exception {
		regularUpdateMessageConverter = new NotificationMessageConverter(mmicrcAlgorithm, trackerInfoCreator);
	}

	@Test
	public void testGetMessageType() {
		assertEquals(EXPECTED_MESSAGE_TYPE,regularUpdateMessageConverter.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}

	@Test
	public void testCreateModel() {
		TrackerNotification trackerNotification = getExpectedTrackerNotification();
		DeviceInfo deviceInfo = regularUpdateMessageConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(NOTIFICATION_MESSAGE_HEX, getClass()));
		assertEquals(MANUFACTURER,deviceInfo.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(MODEL_ID,deviceInfo.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
		assertEquals("301071500007", deviceInfo.getDevice().getDeviceId(),
				"Expected device Id and Actual device Id are not same");
		assertEquals(EXPECTED_MESSAGE_TYPE,deviceInfo.getMessageType(),
				"Expected and actual device data are not same");
		assertEquals(trackerNotification,deviceInfo.getDeviceData().get(TrackerNotification.TRACKER_NOTIF),
				"Expected Tracker Notification and Actual Tracker Notification are not same");
	}

	private TrackerNotification getExpectedTrackerNotification() {
		List<TrackerInfo> notificationData = new ArrayList<>();
		TrackerInfo trackerInfo = trackerInfoCreator
				.constructTrackerInfo(createBinaryPayloadFromHexaPayload(DEVICE_INFO_TEST_DATA, getClass()));
		notificationData.add(trackerInfo);
		return new TrackerNotification(NotificationType.REGULAR_DATA, 1, notificationData);
	}

}
