package com.hpe.iot.dc.trinetra.southbound.transformer.impl;

import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IPUPDATE_DATA_FRAME_HEX;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.STANDARD_MSG_DATA;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl;
import com.hpe.iot.dc.trinetra.model.TrinetraDeviceModel;
import com.hpe.iot.dc.trinetra.southbound.converter.impl.IpAddressMessageConverter;
import com.hpe.iot.dc.trinetra.southbound.converter.impl.StandardMessageConverter;
import com.hpe.iot.dc.trinetra.southbound.converter.impl.StandardMessageCreator;
import com.hpe.iot.dc.udp.utility.TestUtility;
import com.handson.iot.dc.util.DataParserUtility;

/**
 * @author sveera
 *
 */
public class TrinetraDataModelTransformerTest {

	private static final String[] INVALID_DATA_FRAME = new String[] { "39", "34", "23", "3E" };

	private static final String[] INVALID_MSG_TYP_DATA_FRAME_HEX = new String[] { "3C", "35", "42", "00", "7B", "2E",
			"00", "FF", "FF", "22", "32", "00", "00", "00", "00", "2E", "B8", "A1", "F5", "2C", "0B", "49", "00", "00",
			"0C", "0A", "31", "08", "90", "8D", "7A", "3E" };

	private StandardMessageCreator standardMessageCreator = new StandardMessageCreator();
	private IpAddressMessageConverter ipAddressMetaModelConverter = new IpAddressMessageConverter();
	private StandardMessageConverter standardMessageMetaModelConverter = new StandardMessageConverter(
			standardMessageCreator);
	private TrinetraDataModelTransformer trinetraDataModelTransformer;
	private UplinkDeviceDataConverterFactory metaModelConverterFactory;
	private DeviceModel deviceModel = new TrinetraDeviceModel();

	@Before
	public void setUp() throws Exception {
		List<UplinkDeviceDataConverter> messageConverters = new ArrayList<>();
		messageConverters.add(ipAddressMetaModelConverter);
		messageConverters.add(standardMessageMetaModelConverter);
		metaModelConverterFactory = new UplinkDeviceDataConverterFactoryImpl(messageConverters);
		trinetraDataModelTransformer = new TrinetraDataModelTransformer(metaModelConverterFactory);
	}

	@Test
	public void testTrinetraDataModelTransformer() {
		Assert.assertNotNull("TrinetraDataModelTransformer Cannot be Null", trinetraDataModelTransformer);
	}

	@Test(expected = EmptyUDPMessage.class)
	public void testForNull() {
		trinetraDataModelTransformer.convertToModel(deviceModel, null);
	}

	@Test
	public void testForEmptyBytes() {
		byte[] data = new byte[1024];
		List<DeviceInfo> dataFrames = trinetraDataModelTransformer.convertToModel(deviceModel, data);
		Assert.assertEquals("Data Frames length should be Zero ", 0, dataFrames.size());
	}

	@Test
	public void testOneDeviceInformationDataFrame() {
		byte[] inputData = TestUtility.createDataWithPaddingBytes(this.getClass(), IPUPDATE_DATA_FRAME_HEX);
		List<DeviceInfo> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX));
		List<DeviceInfo> actualDataModels = trinetraDataModelTransformer.convertToModel(deviceModel, inputData);
		Assert.assertEquals("Expected and Actual Data Frames are not equal.", expectedDataModels, actualDataModels);
	}

	@Test
	public void testTwoDeviceInformationDataFrames() {
		byte[] inputData = TestUtility.createDataWithPaddingBytes(this.getClass(), IPUPDATE_DATA_FRAME_HEX,
				IPUPDATE_DATA_FRAME_HEX);
		List<DeviceInfo> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX),
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX));
		List<DeviceInfo> actualDataModels = trinetraDataModelTransformer.convertToModel(deviceModel, inputData);
		Assert.assertEquals("Expected and Actual Data Frames are not equal.", expectedDataModels, actualDataModels);
	}

	@Test
	public void testForTwoDeviceInformationDataFramesWithInvalidMessageAtMiddle() {
		byte[] inputData = TestUtility.createDataWithPaddingBytes(this.getClass(), IPUPDATE_DATA_FRAME_HEX,
				INVALID_DATA_FRAME, IPUPDATE_DATA_FRAME_HEX);
		List<DeviceInfo> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX),
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX));
		List<DeviceInfo> actualDataModels = trinetraDataModelTransformer.convertToModel(deviceModel, inputData);
		Assert.assertEquals("Expected and Actual Data Frames are not equal.", expectedDataModels, actualDataModels);
	}

	@Test
	public void testForMixedDataFrameTypes() {
		byte[] inputData = TestUtility.createDataWithPaddingBytes(this.getClass(), IPUPDATE_DATA_FRAME_HEX,
				INVALID_DATA_FRAME, STANDARD_MSG_DATA);
		List<DeviceInfo> expectedDataModels = createExpctedDataModel(
				new KnownMessageTypeFrame(ipAddressMetaModelConverter.getMessageType(), IPUPDATE_DATA_FRAME_HEX),
				new KnownMessageTypeFrame(standardMessageMetaModelConverter.getMessageType(), STANDARD_MSG_DATA));
		List<DeviceInfo> actualDataModels = trinetraDataModelTransformer.convertToModel(deviceModel, inputData);
		Assert.assertEquals("Expected and Actual Data Frames are not equal.", expectedDataModels, actualDataModels);
	}

	@Test
	public void testForValidDataFrameWithInvalidMessageType() {
		byte[] inputData = TestUtility.createDataWithPaddingBytes(this.getClass(), INVALID_MSG_TYP_DATA_FRAME_HEX);
		List<DeviceInfo> expectedDataModels = new ArrayList<>();
		List<DeviceInfo> actualDataModels = trinetraDataModelTransformer.convertToModel(deviceModel, inputData);
		Assert.assertEquals("Expected and Actual Data Frames are not equal.", expectedDataModels, actualDataModels);
	}

	private List<DeviceInfo> createExpctedDataModel(KnownMessageTypeFrame... knownMessageTypeFrames) {
		List<DeviceInfo> expectedDataFrames = new ArrayList<>();
		for (KnownMessageTypeFrame knownMessageTypeFrame : knownMessageTypeFrames) {
			UplinkDeviceDataConverter metaModelConverter = metaModelConverterFactory
					.getModelConverter(knownMessageTypeFrame.getMessageType());
			DeviceInfo dataModel = metaModelConverter.createModel(deviceModel, DataParserUtility
					.createBinaryPayloadFromHexaPayload(knownMessageTypeFrame.getDataFrameHex(), this.getClass()));
			expectedDataFrames.add(dataModel);
		}
		return expectedDataFrames;
	}

}
