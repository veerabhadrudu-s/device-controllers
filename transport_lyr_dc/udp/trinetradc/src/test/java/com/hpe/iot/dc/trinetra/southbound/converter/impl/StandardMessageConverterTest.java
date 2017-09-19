package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IGNITION_OFF_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IGNITION_ON_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA_1;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceData;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.model.Notification;
import com.hpe.iot.dc.trinetra.model.NotificationRecord;
import com.hpe.iot.dc.trinetra.model.TrinetraDeviceModel;
import com.hpe.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
public class StandardMessageConverterTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private StandardMessageConverter standardMessageConverter;
	private TrinetraDeviceModel deviceModel = new TrinetraDeviceModel();

	@Before
	public void setUp() throws Exception {
		standardMessageConverter = new StandardMessageConverter(new StandardMessageCreator());
	}

	@Test
	public void testStandardMessageConverter() {
		Assert.assertNotNull("Standard Message Converter can't be null", standardMessageConverter);
	}

	@Test
	public void testSupportedMessageTypes() {
		List<String> expectedSupportedMessageTypes = Arrays.<String>asList("$", "S", "D", "s", "H", "G", "F", "_", "I",
				"i", "P", "L", "Z", "C", "b", "B", "a", "E", "m", "f", "e", "l");
		Assert.assertSame("Expected and actual Operators are not same", expectedSupportedMessageTypes.size(),
				standardMessageConverter.getMessageTypes().size());
		Assert.assertThat("Expected and actual Operators are not same", standardMessageConverter.getMessageTypes(),
				IsIterableContainingInOrder.contains(expectedSupportedMessageTypes.toArray()));
	}

	@Test
	public void testGetMessageType() {
		Assert.assertSame("Expected and actual Operators are not same", "$", standardMessageConverter.getMessageType());
	}

	@Test
	public void testCreateModelForStandardMsgType() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		Map<String, DeviceData> deviceInformation = dataModel.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		Assert.assertEquals("Expected Number of records and Actual Number of records are not same", 1,
				notification.getNoOfRecords());
		Assert.assertEquals("Expected Message Type and Actual Message Type are not same", "$",
				dataModel.getMessageType());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not same", "16949",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Odometer reading and Actual reading are not same", "50",
				notificationRecord.getOdometer());
		Assert.assertEquals("Expected Latitude and Actual Latitude are not same", "94484526N",
				notificationRecord.getLatitude());
		Assert.assertEquals("Expected Longitude and Actual Longitude are not same", "1225468958W",
				notificationRecord.getLongitude());
		Assert.assertEquals("Expected Time and Actual Time are not same", "10:49:08", notificationRecord.getTime());
		Assert.assertEquals("Expected Date and Actual Date are not same", "11/17/2016", notificationRecord.getDate());
		Assert.assertEquals("Expected and Actual Speed are not same", "0Mph", notificationRecord.getSpeed());
	}

	@Test
	public void testCreateModelForStandardMsgType1() {
		byte[] ignitionOnData = getStandardMessage(STANDARD_MSG_DATA_1);
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		logger.debug("Converted Data Model is " + dataModel);
	}

	@Test
	public void testCreateModelIgnitionOnForSensorMessageType() {
		byte[] ignitionOnData = getIgnitionOnMessage();
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		Map<String, DeviceData> deviceInformation = dataModel.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		Assert.assertEquals("Expected Number of records and Actual Number of records are not same", 1,
				notification.getNoOfRecords());
		Assert.assertEquals("Expected Message Type and Actual Message Type are not same", "S",
				dataModel.getMessageType());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not same", "16949",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Odometer reading and Actual reading are not same", "50",
				notificationRecord.getOdometer());
		Assert.assertEquals("Expected Latitude and Actual Latitude are not same", "94484526N",
				notificationRecord.getLatitude());
		Assert.assertEquals("Expected Longitude and Actual Longitude are not same", "1225468958W",
				notificationRecord.getLongitude());
		Assert.assertEquals("Expected Time and Actual Time are not same", "10:49:08", notificationRecord.getTime());
		Assert.assertEquals("Expected Date and Actual Date are not same", "11/17/2016", notificationRecord.getDate());
		Assert.assertEquals("Expected Ignition status and Actual status are not same", 1,
				notificationRecord.getCustomInfo().get("Ignition"));
		Assert.assertEquals("Expected and Actual Speed are not same", "0Mph", notificationRecord.getSpeed());
	}

	@Test
	public void testCreateModelIgnitionOffForSensorMessageType() {
		byte[] ignitionOnData = getIgnitionOFFMessage();
		DeviceInfo dataModel = standardMessageConverter.createModel(deviceModel, ignitionOnData);
		Map<String, DeviceData> deviceInformation = dataModel.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		Assert.assertEquals("Expected Number of records and Actual Number of records are not same", 1,
				notification.getNoOfRecords());
		Assert.assertEquals("Expected Message Type and Actual Message Type are not same", "S",
				dataModel.getMessageType());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not same", "16949",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Odometer reading and Actual reading are not same", "50",
				notificationRecord.getOdometer());
		Assert.assertEquals("Expected Latitude and Actual Latitude are not same", "94484526N",
				notificationRecord.getLatitude());
		Assert.assertEquals("Expected Longitude and Actual Longitude are not same", "1225468958W",
				notificationRecord.getLongitude());
		Assert.assertEquals("Expected Time and Actual Time are not same", "10:49:08", notificationRecord.getTime());
		Assert.assertEquals("Expected Date and Actual Date are not same", "11/17/2016", notificationRecord.getDate());
		Assert.assertEquals("Expected Ignition status and Actual status are not same", 0,
				notificationRecord.getCustomInfo().get("Ignition"));
		Assert.assertEquals("Expected and Actual Speed are not same", "0Mph", notificationRecord.getSpeed());
	}

	private byte[] getIgnitionOnMessage() {
		return DataParserUtility.createBinaryPayloadFromHexaPayload(IGNITION_ON_DATA, this.getClass());
	}

	private byte[] getIgnitionOFFMessage() {
		return DataParserUtility.createBinaryPayloadFromHexaPayload(IGNITION_OFF_DATA, this.getClass());
	}

	private byte[] getStandardMessage(String[] STANDARD_MSG_DATA) {
		return DataParserUtility.createBinaryPayloadFromHexaPayload(STANDARD_MSG_DATA, this.getClass());
	}

}
