/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.southbound.converter.impl;

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.hpe.iot.dc.mmi.drivemate.EventIdToNameMapper
import com.hpe.iot.dc.mmi.drivemate.Notification
import com.hpe.iot.dc.mmi.drivemate.NotificationRecord
import com.hpe.iot.dc.mmi.drivemate.UplinkNotificationMessageConverter
import com.hpe.iot.dc.mmi.drivemate.testdata.MMIDrivemateTestDataCollection
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.util.DataParserUtility

import static org.junit.Assert.*

/**
 * @author sveera
 *
 */
class UplinkNotificationMessageConverterTest {

	private static final String NOTIFICATION = "notification";

	private static final DeviceImpl DEVICE_UNDER_TEST = new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
	MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345");

	private UplinkNotificationMessageConverter uplinkNotificationMessageConverter;
	private EventIdToNameMapper eventIdToNameMapper=new EventIdToNameMapper();

	@Before
	public void setUp() throws Exception {
		uplinkNotificationMessageConverter=new UplinkNotificationMessageConverter(eventIdToNameMapper);
	}

	@Test
	public void testNotificationUplinkMessageConverter() {
		Assert.assertNotNull("UplinkNotificationMessageConverter cannot be null",uplinkNotificationMessageConverter);
	}

	@Test
	public void testCreateModel(){
		DeviceInfo expectedDeviceInfo=createExpectedDeviceInfo();
		DeviceInfo actualDeviceInfo=uplinkNotificationMessageConverter.createModel(
				DEVICE_UNDER_TEST,
				DataParserUtility.createBinaryPayloadFromHexaPayload(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,getClass()));
		Assert.assertEquals("Expected DeviceInfo and Actual Device Info are not same.",expectedDeviceInfo,actualDeviceInfo);
	}

	private DeviceInfo createExpectedDeviceInfo(){
		DeviceInfo expectedDeviceInfo=new DeviceInfo(DEVICE_UNDER_TEST, NOTIFICATION,
				DataParserUtility.createBinaryPayloadFromHexaPayload(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,getClass()));
		List<NotificationRecord> notificationRecords=new ArrayList<>();
		Map<String,Object> record1events=new LinkedHashMap<>();
		record1events.put("GPSSatelliteUsed",14);
		record1events.put("GPSAltitude","96");
		record1events.put("Ignition","0");
		NotificationRecord recordOne=new NotificationRecord("1430292733","54.689561","25.2509005","96","0",14,0);
		recordOne.getCustomInfo().putAll(record1events);
		Map<String,Object> record2events=new LinkedHashMap<>();
		record2events.put("GPSSatelliteUsed",15);
		record2events.put("GPSAltitude","97");
		record2events.put("Ignition","0");
		NotificationRecord recordTwo=new NotificationRecord("1430292713","54.6895598","25.2509058", "97", "0", 15, 0);
		recordTwo.getCustomInfo().putAll(record2events);
		notificationRecords.add(recordOne);
		notificationRecords.add(recordTwo);
		Notification notification=new Notification(2,notificationRecords);
		expectedDeviceInfo.addDeviceData(notification.getDeviceDataInformation(),notification);
		return expectedDeviceInfo;
	}
}
