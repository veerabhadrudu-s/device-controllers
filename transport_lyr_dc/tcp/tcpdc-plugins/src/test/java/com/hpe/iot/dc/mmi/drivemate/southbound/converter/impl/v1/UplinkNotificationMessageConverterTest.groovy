/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.southbound.converter.impl.v1;

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.hpe.iot.dc.mmi.drivemate.v1.EventIdToNameMapper
import com.hpe.iot.dc.mmi.drivemate.v1.GPSInfo
import com.hpe.iot.dc.mmi.drivemate.v1.Notification
import com.hpe.iot.dc.mmi.drivemate.v1.NotificationRecord
import com.hpe.iot.dc.mmi.drivemate.v1.UplinkNotificationMessageConverter
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
	MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_1,"123456789012345");

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
		String eventType="Regular Update";
		GPSInfo gpsInfoOne=new GPSInfo("Wed, 29 Apr 2015", 0, "546895610","252509005", "96", "0", 14, 0);
		Map<String,String> events=new HashMap<>();
		events.put("Ignition","0");
		NotificationRecord recordOne=new NotificationRecord(gpsInfoOne, eventType, events);
		GPSInfo gpsInfoTwo=new GPSInfo("Wed, 29 Apr 2015", 0, "546895598","252509058", "97", "0", 15, 0);
		NotificationRecord recordTwo=new NotificationRecord(gpsInfoTwo, eventType, events);
		notificationRecords.add(recordOne);
		notificationRecords.add(recordTwo);
		Notification notification=new Notification(2,notificationRecords);
		expectedDeviceInfo.addDeviceData(notification.getDeviceDataInformation(),notification);
		return expectedDeviceInfo;
	}
}
