/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.southbound.transformer.impl;

import static com.hpe.iot.dc.mmi.utility.TestUtility.createDataWithPaddingBytes

import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import com.hpe.iot.dc.mmi.drivemate.EventIdToNameMapper
import com.hpe.iot.dc.mmi.drivemate.MMIDrivemateDataModelTransformer
import com.hpe.iot.dc.mmi.drivemate.UplinkNotificationMessageConverter
import com.hpe.iot.dc.mmi.drivemate.testdata.MMIDrivemateTestDataCollection
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl
import com.hpe.iot.dc.util.DataParserUtility



/**
 * @author sveera
 *
 */
public class MMIDrivemateDataModelTransformerTest {

	private static final String HAND_SHAKE = "HandShake";
	private static final DeviceImpl DEVICE_UNDER_TEST = new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
	MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345");

	private UplinkNotificationMessageConverter uplinkNotificationMessageConverter;
	private MMIDrivemateDataModelTransformer mmiDrivemateDataModelTransformer;
	private UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory;

	@Before
	public void setUp() throws Exception {
		List<UplinkDeviceDataConverter> uplinkConverters=new ArrayList<>();
		uplinkNotificationMessageConverter=new UplinkNotificationMessageConverter(new EventIdToNameMapper());
		uplinkConverters.add(uplinkNotificationMessageConverter);
		uplinkDeviceDataConverterFactory=new UplinkDeviceDataConverterFactoryImpl(uplinkConverters);
		mmiDrivemateDataModelTransformer=new MMIDrivemateDataModelTransformer(uplinkDeviceDataConverterFactory);
	}

	@Test
	public void testMMIDrivemateDataModelTransformer() {
		Assert.assertNotNull("MMIDrivemateDataModelTransformer cannot be null",mmiDrivemateDataModelTransformer);
	}

	@Test
	public void testForHandShakeMessage() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),MMIDrivemateTestDataCollection.HAND_SHAKE_PAYLOAD);
		List<DeviceInfo> expectedDeviceInfo=createExpectedDataInfoForHandshakeMessage(inputData);
		List<DeviceInfo> actualDeviceInfo=mmiDrivemateDataModelTransformer.convertToModel(new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345"),inputData);
		Assert.assertEquals("Expected DeviceInfo and Actual DeviceInfo are not same",expectedDeviceInfo,actualDeviceInfo);
	}

	@Test
	public void testForNotificationMessage() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD);
		List<DeviceInfo> expectedDeviceInfo=createExpectedDataInfoForNotificationMessage(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD);
		List<DeviceInfo> actualDeviceInfo=mmiDrivemateDataModelTransformer.convertToModel(new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345"),inputData);
		Assert.assertEquals("Expected DeviceInfo and Actual DeviceInfo are not same",expectedDeviceInfo,actualDeviceInfo);
	}
	
	@Ignore
	@Test
	public void testForNotificationMessage2() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD_2);
		List<DeviceInfo> expectedDeviceInfo=createExpectedDataInfoForNotificationMessage(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD_2);
		List<DeviceInfo> actualDeviceInfo=mmiDrivemateDataModelTransformer.convertToModel(new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,"123456789012345"),inputData);
		Assert.assertEquals("Expected DeviceInfo and Actual DeviceInfo are not same",expectedDeviceInfo,actualDeviceInfo);
	}

	@Test
	public void testForTwoNotificationMessage() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD);
		List<DeviceInfo> expectedDeviceInfo=createExpectedDataInfoForNotificationMessage(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD);
		List<DeviceInfo> actualDeviceInfo=mmiDrivemateDataModelTransformer.convertToModel(new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345"),inputData);
		Assert.assertEquals("Expected DeviceInfo and Actual DeviceInfo are not same",expectedDeviceInfo,actualDeviceInfo);
	}

	@Test
	public void testForTwoNotificationMessageWithTruncatedPayload() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,
				MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,MMIDrivemateTestDataCollection.TRUNCATED_NOTIFICATION_PAYLOAD);
		List<DeviceInfo> expectedDeviceInfo=createExpectedDataInfoForNotificationMessage(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD,MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD);
		List<DeviceInfo> actualDeviceInfo=mmiDrivemateDataModelTransformer.convertToModel(new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345"),inputData);
		Assert.assertEquals("Expected DeviceInfo and Actual DeviceInfo are not same",expectedDeviceInfo,actualDeviceInfo);
	}

	private List<DeviceInfo> createExpectedDataInfoForHandshakeMessage(byte[] inputData){
		List<DeviceInfo> expectedDeviceInfo=new ArrayList<>();
		DeviceInfo deviceInfo=new DeviceInfo(DEVICE_UNDER_TEST,HAND_SHAKE, DataParserUtility.truncateEmptyBytes(inputData));
		expectedDeviceInfo.add(deviceInfo);
		return expectedDeviceInfo;
	}

	private List<DeviceInfo> createExpectedDataInfoForNotificationMessage(String[] ... dataFrameHex){
		List<DeviceInfo> deviceInfos=new ArrayList<>();
		for(String[] dataFrame in dataFrameHex)
			deviceInfos.add(uplinkNotificationMessageConverter.createModel(
					DEVICE_UNDER_TEST,
					DataParserUtility.createBinaryPayloadFromHexaPayload(dataFrame,getClass())));
		return deviceInfos;
	}

	private List<DeviceInfo> createExpectedDataInfoForNotificationMessage(String[] dataFrameHex){
		List<DeviceInfo> deviceInfos=new ArrayList<>();
		deviceInfos.add(uplinkNotificationMessageConverter.createModel(
				DEVICE_UNDER_TEST,
				DataParserUtility.createBinaryPayloadFromHexaPayload(dataFrameHex,getClass())));
		return deviceInfos;
	}
}
