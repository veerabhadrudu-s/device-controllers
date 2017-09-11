/**
 * 
 */
package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.COMBIND_STANDARD_MSG_DATA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	@Before
	public void setUp() throws Exception {
		combinedStandardMessageConverter = new CombinedStandardMessageConverter(standardMessageCreator);
	}

	@Test
	public void testGetMessageType() {
		Assert.assertEquals("Expected Message Type and Actual Message Type are not same", "y",
				combinedStandardMessageConverter.getMessageType());
	}

	@Test
	public void testCreateModelForCombinedMessageType() {
		byte[] ignitionOnData = getCombinedStandardMessage();
		DeviceInfo dataModel = combinedStandardMessageConverter.createModel(deviceModel, ignitionOnData);
		Map<String, DeviceData> deviceInformation = dataModel.getDeviceData();
		Notification notification = (Notification) deviceInformation.get(Notification.NOTIF_MESS_TYP);
		NotificationRecord notificationRecord = notification.getNotifications().get(0);
		Assert.assertEquals("Expected Number of records and Actual Number of records are not same", 10,
				notification.getNoOfRecords());
		Assert.assertEquals("Expected Number of records and Actual Number of records are not same", notification.getNoOfRecords(),
				notification.getNotifications().size());
		Assert.assertEquals("Expected Message Type and Actual Message Type are not same", "y",
				dataModel.getMessageType());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not same", "6945",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Odometer reading and Actual reading are not same", "1",
				notificationRecord.getOdometer());
		Assert.assertEquals("Expected Latitude and Actual Latitude are not same", "0N",
				notificationRecord.getLatitude());
		Assert.assertEquals("Expected Longitude and Actual Longitude are not same", "0W",
				notificationRecord.getLongitude());
		Assert.assertEquals("Expected Time and Actual Time are not same", "01:01:46", notificationRecord.getTime());
		Assert.assertEquals("Expected Date and Actual Date are not same", "03/03/2127", notificationRecord.getDate());
	}

	private byte[] getCombinedStandardMessage() {
		return DataParserUtility.createBinaryPayloadFromHexaPayload(COMBIND_STANDARD_MSG_DATA, this.getClass());
	}

}
