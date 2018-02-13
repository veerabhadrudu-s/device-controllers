package com.hpe.iot.dc.mmi.safemate.southbound.transformer.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.IPCONNECT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.NOTIFICATION_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.utility.TestUtility.createDataWithPaddingBytes
import static com.handson.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertSame

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.IPConnectMessageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIDataModelTransformer
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.NotificationMessageConverter
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl

/**
 * @author sveera
 *
 */
public class MMIDataModelTransformerTest {

	private static final String[] INVALID_DATA_FRAME = ["39", "34", "23", "3E" ] as  String[];

	private static final String[] INVALID_MSG_TYP_DATA_FRAME_HEX = [
		"3C",
		"35",
		"42",
		"00",
		"7B",
		"2E",
		"00",
		"FF",
		"FF",
		"22",
		"32",
		"00",
		"00",
		"00",
		"00",
		"2E",
		"B8",
		"A1",
		"F5",
		"2C",
		"0B",
		"49",
		"00",
		"00",
		"0C",
		"0A",
		"31",
		"08",
		"90",
		"8D",
		"7A",
		"3E"
	] as String[];

	private MMICRCAlgorithm mmicrcAlgorithm=new MMICRCAlgorithm();
	private TrackerInfoCreator standardMessageCreator = new TrackerInfoCreator();
	private IPConnectMessageConverter ipAddressMetaModelConverter = new IPConnectMessageConverter(mmicrcAlgorithm,standardMessageCreator);
	private NotificationMessageConverter standardMessageMetaModelConverter = new NotificationMessageConverter(mmicrcAlgorithm,
	standardMessageCreator);
	private MMIDataModelTransformer mmiDataModelTransformer;
	private UplinkDeviceDataConverterFactory metaModelConverterFactory;

	@BeforeEach
	public void setUp() throws Exception {
		List<UplinkDeviceDataConverter> messageConverters = new ArrayList<>();
		messageConverters.add(ipAddressMetaModelConverter);
		messageConverters.add(standardMessageMetaModelConverter);
		metaModelConverterFactory = new UplinkDeviceDataConverterFactoryImpl(messageConverters);
		mmiDataModelTransformer = new MMIDataModelTransformer(metaModelConverterFactory);
	}

	@Test
	public void testMMIDataModelTransformer() {
		assertNotNull(mmiDataModelTransformer,"TrinetraDataModelTransformer Cannot be Null");
	}

	@Test
	public void testForEmptyBytes() {
		byte[] data = new byte[1024];
		List<DeviceData> dataFrames = mmiDataModelTransformer.convertToModel(new MMIServerSocketToDeviceModel(),data);
		assertSame(0, dataFrames.size(),"Data Frames length should be Zero");
	}

	@Test
	public void testOneDeviceInformationDataFrame() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),IPCONNECT_DATA_MESSAGE_HEX);
		List<DeviceData> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX));
		List<DeviceData> actualDataModels = mmiDataModelTransformer.convertToModel(new  MMIServerSocketToDeviceModel(),inputData);
		assertEquals( expectedDataModels, actualDataModels,"Expected and Actual Data are not equal");
	}

	@Test
	public void testTwoDeviceInformationDataFrames() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),IPCONNECT_DATA_MESSAGE_HEX, IPCONNECT_DATA_MESSAGE_HEX);
		List<DeviceData> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX),
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX));
		List<DeviceData> actualDataModels = mmiDataModelTransformer.convertToModel(new  MMIServerSocketToDeviceModel(),inputData);
		assertEquals( expectedDataModels, actualDataModels,"Expected and Actual Data are not equal");
	}

	@Test
	@Disabled
	public void testForTwoDeviceInformationDataFramesWithInvalidMessageAtMiddle() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),IPCONNECT_DATA_MESSAGE_HEX, INVALID_DATA_FRAME,
				IPCONNECT_DATA_MESSAGE_HEX);
		List<DeviceInfo> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX),
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX));
		List<DeviceInfo> actualDataModels = mmiDataModelTransformer.convertToModel(new MMIServerSocketToDeviceModel(),inputData);
		assertEquals( expectedDataModels, actualDataModels,"Expected and Actual Data are not equal");
	}

	@Test
	@Disabled
	public void testForMixedDataFrameTypes() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),IPCONNECT_DATA_MESSAGE_HEX, INVALID_DATA_FRAME,
				NOTIFICATION_MESSAGE_HEX);
		List<DeviceData> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPCONNECT_DATA_MESSAGE_HEX),
				new KnownMessageTypeFrame(standardMessageMetaModelConverter.getMessageType(),
				NOTIFICATION_MESSAGE_HEX));
		List<DeviceData> actualDataModels = mmiDataModelTransformer.convertToModel(new MMIServerSocketToDeviceModel(),inputData);
		assertEquals( expectedDataModels, actualDataModels,"Expected and Actual Data are not equal");
	}

	@Test
	public void testForValidDataFrameWithInvalidMessageType() {
		byte[] inputData = createDataWithPaddingBytes(this.getClass(),INVALID_MSG_TYP_DATA_FRAME_HEX);
		List<DeviceData> expectedDataModels = new ArrayList<>();
		List<DeviceData> actualDataModels = mmiDataModelTransformer.convertToModel(new MMIServerSocketToDeviceModel(),inputData);
		assertEquals( expectedDataModels, actualDataModels,"Expected and Actual Data are not equal");
	}



	private List<DeviceInfo> createExpctedDataModel(KnownMessageTypeFrame... knownMessageTypeFrames) {
		List<DeviceInfo> expectedDataFrames = new ArrayList<>();
		for (KnownMessageTypeFrame knownMessageTypeFrame : knownMessageTypeFrames) {
			UplinkDeviceDataConverter metaModelConverter = metaModelConverterFactory
					.getModelConverter(knownMessageTypeFrame.getMessageType());
			DeviceInfo dataModel = metaModelConverter
					.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(knownMessageTypeFrame.getDataFrameHex(),this.getClass()));
			expectedDataFrames.add(dataModel);
		}
		return expectedDataFrames;
	}
}
