package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IGNITION_OFF_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IGNITION_ON_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA_1;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA_2;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA_3;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceData;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.model.Notification;
import com.hpe.iot.dc.trinetra.model.NotificationRecord;
import com.hpe.iot.dc.trinetra.model.TrinetraDeviceModel;
import com.handson.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
public class StandardMessageConverterTest {

	private StandardMessageConverter standardMessageConverter;
	private TrinetraDeviceModel deviceModel = new TrinetraDeviceModel();

	@BeforeEach
	public void setUp() throws Exception {
		standardMessageConverter = new StandardMessageConverter(new StandardMessageCreator());
	}

	@Test
	public void testStandardMessageConverter() {
		assertNotNull(standardMessageConverter, "Standard Message Converter can't be null");
	}

	@Test
	public void testSupportedMessageTypes() {
		List<String> expectedSupportedMessageTypes = Arrays.<String>asList("$", "S", "D", "s", "H", "G", "F", "_", "I",
				"i", "P", "L", "Z", "C", "b", "B", "a", "E", "m", "f", "e", "l");
		assertSame(expectedSupportedMessageTypes.size(), standardMessageConverter.getMessageTypes().size(),
				"Expected and actual Operators are not same");
		MatcherAssert.assertThat("Expected and actual Operators are not same", standardMessageConverter.getMessageTypes(),
				IsIterableContainingInOrder.contains(expectedSupportedMessageTypes.toArray()));

	}

	@Test
	public void testGetMessageType() {
		assertSame("$", standardMessageConverter.getMessageType(), "Expected and actual Operators are not same");
	}

	@Test
	public void testCreateModelForStandardMsgType() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertNotificationMessageTypePayload(1, "$", "16949", "50", "94.807544", "76.986372", "10:49:08", "11/17/2016",
				"0Mph", dataModel);
	}

	@Test
	public void testCreateModelForStandardMsgType1() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA_1);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertNotificationMessageTypePayload(1, "i", "6945", "1", "23.237887", "77.434077", "05:06:45", "09/22/2017",
				"0Mph", dataModel);
	}

	@Test
	public void testCreateModelForStandardMsgType2() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA_2);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertNotificationMessageTypePayload(1, "$", "999992", "1", "10.9974", "76.987119", "07:43:33", "09/22/2017",
				"0Mph", dataModel);
	}

	@Test
	public void testCreateModelForStandardMsgType3() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA_3);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertNotificationMessageTypePayload(1, "$", "6945", "1", "23.235104", "77.435", "08:35:03", "09/22/2017",
				"4Mph", dataModel);
	}

	@Test
	public void testCreateModelIgnitionOnForSensorMessageType() {
		byte[] ignitionOnData = getIgnitionOnMessage();
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertIgnitionMessageTypePayload(1, "S", "16949", "50", "94.807544", "76.986372", "10:49:08", "11/17/2016",
				"0Mph", 1, dataModel);
	}

	@Test
	public void testCreateModelIgnitionOffForSensorMessageType() {
		byte[] ignitionOnData = getIgnitionOFFMessage();
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		assertIgnitionMessageTypePayload(1, "S", "16949", "50", "94.807544", "76.986372", "10:49:08", "11/17/2016",
				"0Mph", 0, dataModel);
	}

	private void assertNotificationMessageTypePayload(int expectedNoOfRecords, String expectedMessageType,
			String expectedDeviceId, String expectedOdometerReading, String expectedLatitude, String expectedLongitude,
			String expectedTime, String expectedDate, String expectedMph, DeviceInfo deviceInfo) {
		Notification notification = readNotification(expectedNoOfRecords, deviceInfo);
		assertDevice(expectedMessageType, deviceInfo.getMessageType(), expectedDeviceId, deviceInfo.getDevice());
		assertNotficationRecord(expectedOdometerReading, expectedLatitude, expectedLongitude, expectedTime,
				expectedDate, expectedMph, notification.getNotifications().get(0));
	}

	private void assertIgnitionMessageTypePayload(int expectedNoOfRecords, String expectedMessageType,
			String expectedDeviceId, String expectedOdometerReading, String expectedLatitude, String expectedLongitude,
			String expectedTime, String expectedDate, String expectedMph, int expectedIgnitionStatus,
			DeviceInfo deviceInfo) {
		Notification notification = readNotification(expectedNoOfRecords, deviceInfo);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		assertDevice(expectedMessageType, deviceInfo.getMessageType(), expectedDeviceId, deviceInfo.getDevice());
		assertNotficationRecord(expectedOdometerReading, expectedLatitude, expectedLongitude, expectedTime,
				expectedDate, expectedMph, notificationRecord);
		assertEquals(expectedIgnitionStatus, notificationRecord.getCustomInfo().get("Ignition"),
				"Expected Ignition status and Actual status are not same");
	}

	private Notification readNotification(int expectedNoOfRecords, DeviceInfo deviceInfo) {
		Map<String, DeviceData> deviceInformation = deviceInfo.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		assertEquals(expectedNoOfRecords, notification.getNoOfRecords(),
				"Expected Number of records and Actual Number of records are not same");
		return notification;
	}

	private void assertDevice(String expectedMessageType, String actualMessageType, String expectedDeviceId,
			Device device) {
		assertEquals(expectedMessageType, actualMessageType,
				"Expected Message Type and Actual Message Type are not same");
		assertEquals(TRINETRA, device.getManufacturer(), "Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(VEHICAL_TRACKING, device.getModelId(), "Expected Model ID and Actual Model ID are not same");
		assertEquals(expectedDeviceId, device.getDeviceId(), "Expected Device Id and Actual Device Id are not same");
	}

	private void assertNotficationRecord(String expectedOdometerReading, String expectedLatitude,
			String expectedLongitude, String expectedTime, String expectedDate, String expectedMph,
			NotificationRecord notificationRecord) {
		assertEquals(expectedOdometerReading, notificationRecord.getOdometer(),
				"Expected Odometer reading and Actual reading are not same");
		assertEquals(expectedLatitude, notificationRecord.getLatitude(),
				"Expected Latitude and Actual Latitude are not same");
		assertEquals(expectedLongitude, notificationRecord.getLongitude(),
				"Expected Longitude and Actual Longitude are not same");
		assertEquals(expectedTime, notificationRecord.getTime(), "Expected Time and Actual Time are not same");
		assertEquals(expectedDate, notificationRecord.getDate(), "Expected Date and Actual Date are not same");
		assertEquals(expectedMph, notificationRecord.getSpeed(), "Expected and Actual Speed are not same");
	}

	private byte[] getIgnitionOnMessage() {
		return DataParserUtility.createDecimalPayloadFromHexaPayload(IGNITION_ON_DATA, this.getClass());
	}

	private byte[] getIgnitionOFFMessage() {
		return DataParserUtility.createDecimalPayloadFromHexaPayload(IGNITION_OFF_DATA, this.getClass());
	}

	private byte[] getStandardMessage(String[] STANDARD_MSG_DATA) {
		return DataParserUtility.createDecimalPayloadFromHexaPayload(STANDARD_MSG_DATA, this.getClass());
	}

}
