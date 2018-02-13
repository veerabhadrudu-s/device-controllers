package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.model.DeviceAddress.DEVICE_ADDRESS;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IPUPDATE_DATA_FRAME_HEX;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.InetAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.model.DeviceAddress;
import com.hpe.iot.dc.trinetra.model.TrinetraDeviceModel;
import com.hpe.iot.dc.udp.model.impl.UDPDeviceImpl;
import com.handson.iot.dc.util.DataParserUtility;

import mockit.Expectations;
import mockit.Mocked;

/**
 * @author sveera
 *
 */
public class IpAddressMessageConverterTest {

	private static final String DATA_GRAM_IP = "106.208.80.48";
	private static final int DATA_GRAM_PORT = 3000;
	private TrinetraDeviceModel deviceModel = new TrinetraDeviceModel();
	private IpAddressMessageConverter ipAddressMetaModelConverter;

	@Mocked
	private InetAddress address;

	@BeforeEach
	public void setUp() throws Exception {
		ipAddressMetaModelConverter = new IpAddressMessageConverter();
	}

	@Test
	public void testGetOperator() {
		assertSame("#", ipAddressMetaModelConverter.getMessageType(), "Expected and actual Operators are not same");
	}

	@Test
	public void testCreateModelForSourceIpAndSourcePortDecoding() {
		DeviceInfo dataModel = ipAddressMetaModelConverter.createModel(deviceModel,
				DataParserUtility.createBinaryPayloadFromHexaPayload(IPUPDATE_DATA_FRAME_HEX, this.getClass()));
		DeviceAddress deviceAddress = (DeviceAddress) dataModel.getDeviceData().get(DEVICE_ADDRESS);
		assertEquals("100.72.84.192", deviceAddress.getDeviceIp(),
				"Expected source Ip and Actual source Ip are not same");
		assertEquals("4038", deviceAddress.getPort(), "Expected source Port and Actual source Port are not Same");
		assertEquals("6945", dataModel.getDevice().getDeviceId(),
				"Expected Device Id and Actual Device Id are not same");
		assertEquals(TRINETRA, dataModel.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(VEHICAL_TRACKING, dataModel.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
	}

	@Test
	public void testCreateModelForSourceIpAndSourcePortDecodingForUDPDevice() {
		new Expectations() {
			{
				address.getHostAddress();
				result = DATA_GRAM_IP;
			}
		};
		DeviceInfo dataModel = ipAddressMetaModelConverter.createModel(
				new UDPDeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion(),
						address, DATA_GRAM_PORT),
				DataParserUtility.createBinaryPayloadFromHexaPayload(IPUPDATE_DATA_FRAME_HEX, this.getClass()));
		DeviceAddress deviceAddress = (DeviceAddress) dataModel.getDeviceData().get(DEVICE_ADDRESS);
		assertEquals(DATA_GRAM_IP, deviceAddress.getDeviceIp(), "Expected source Ip and Actual source Ip are not same");
		assertEquals(Integer.toString(DATA_GRAM_PORT), deviceAddress.getPort(),
				"Expected source Port and Actual source Port are not same");
		assertEquals("6945", dataModel.getDevice().getDeviceId(),
				"Expected Device Id and Actual Device Id are not same");
		assertEquals(TRINETRA, dataModel.getDevice().getManufacturer(),
				"Expected Manufacturer and Actual Manufacturer are not same");
		assertEquals(VEHICAL_TRACKING, dataModel.getDevice().getModelId(),
				"Expected Model ID and Actual Model ID are not same");
	}

}
