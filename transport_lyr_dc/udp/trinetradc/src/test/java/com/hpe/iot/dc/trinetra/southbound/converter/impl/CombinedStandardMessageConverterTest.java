/**
 * 
 */
package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.COMBIND_STANDARD_MSG_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
public class CombinedStandardMessageConverterTest {

	private final StandardMessageCreator standardMessageCreator = new StandardMessageCreator();
	private final TrinetraDeviceModel deviceModel = new TrinetraDeviceModel();
	private CombinedStandardMessageConverter combinedStandardMessageConverter;

	@BeforeEach
	public void setUp() throws Exception {
		combinedStandardMessageConverter = new CombinedStandardMessageConverter(standardMessageCreator);
	}

	@Test
	public void testGetMessageType() {
		assertEquals("y", combinedStandardMessageConverter.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}

	@Test
	public void testCreateModelForCombinedMessageType() {
		byte[] ignitionOnData = getCombinedStandardMessage();
		DeviceInfo dataModel = combinedStandardMessageConverter.createModel(deviceModel, ignitionOnData);
		Map<String, DeviceData> deviceInformation = dataModel.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		assertEquals(10, notification.getNoOfRecords(),
				"Expected Number of records and Actual Number of records are not same");
		assertEquals(notification.getNoOfRecords(), notification.getNotifications().size(),
				"Expected Number of records and Actual Number of records are not same");
		assertEquals("y", dataModel.getMessageType(), "Expected Message Type and Actual Message Type are not same");
		assertEquals(TRINETRA, dataModel.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(VEHICAL_TRACKING, dataModel.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not Same");
		assertEquals("6945", dataModel.getDevice().getDeviceId(),
				"Expected Device Id and Actual Device Id are not same");
		assertEquals("1", notificationRecord.getOdometer(),
				"Expected Odometer reading and Actual reading are not same");
		assertEquals("0.0", notificationRecord.getLatitude(), "Expected Latitude and Actual Latitude are not same");
		assertEquals("0.0", notificationRecord.getLongitude(), "Expected Longitude and Actual Longitude are not same");
		assertEquals("01:01:46", notificationRecord.getTime(), "Expected Time and Actual Time are not same");
		assertEquals("03/03/2127", notificationRecord.getDate(), "Expected Date and Actual Date are not same");
		assertEquals("0Mph", notificationRecord.getSpeed(), "Expected and Actual Speed are not same");
	}

	private byte[] getCombinedStandardMessage() {
		return DataParserUtility.createBinaryPayloadFromHexaPayload(COMBIND_STANDARD_MSG_DATA, this.getClass());
	}

}
