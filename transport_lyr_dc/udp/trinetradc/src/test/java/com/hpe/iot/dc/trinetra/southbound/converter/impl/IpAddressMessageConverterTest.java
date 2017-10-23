package com.hpe.iot.dc.trinetra.southbound.converter.impl;

import static com.hpe.iot.dc.trinetra.model.DeviceAddress.DEVICE_ADDRESS;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.IPUPDATE_DATA_FRAME_HEX;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.TRINETRA;
import static com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData.VEHICAL_TRACKING;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.model.DeviceAddress;
import com.hpe.iot.dc.trinetra.model.TrinetraDeviceModel;
import com.hpe.iot.dc.udp.model.impl.UDPDeviceImpl;
import com.hpe.iot.dc.util.DataParserUtility;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

/**
 * @author sveera
 *
 */
@RunWith(JMockit.class)
public class IpAddressMessageConverterTest {

	private static final String DATA_GRAM_IP = "106.208.80.48";
	private static final int DATA_GRAM_PORT = 3000;
	private TrinetraDeviceModel deviceModel = new TrinetraDeviceModel();
	private IpAddressMessageConverter ipAddressMetaModelConverter;

	@Mocked
	private InetAddress address;

	@Before
	public void setUp() throws Exception {
		ipAddressMetaModelConverter = new IpAddressMessageConverter();
	}

	@Test
	public void testGetOperator() {
		Assert.assertSame("Expected and actual Operators are not same", "#",
				ipAddressMetaModelConverter.getMessageType());
	}

	@Test
	public void testCreateModelForSourceIpAndSourcePortDecoding() {
		DeviceInfo dataModel = ipAddressMetaModelConverter.createModel(deviceModel,
				DataParserUtility.createBinaryPayloadFromHexaPayload(IPUPDATE_DATA_FRAME_HEX, this.getClass()));
		DeviceAddress deviceAddress = (DeviceAddress) dataModel.getDeviceData().get(DEVICE_ADDRESS);
		Assert.assertEquals("Expected source Ip and Actual source Ip are not Same", "100.72.84.192",
				deviceAddress.getDeviceIp());
		Assert.assertEquals("Expected source Port and Actual source Port are not Same", "4038",
				deviceAddress.getPort());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not Same", "6945",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
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
		Assert.assertEquals("Expected source Ip and Actual source Ip are not Same", DATA_GRAM_IP,
				deviceAddress.getDeviceIp());
		Assert.assertEquals("Expected source Port and Actual source Port are not Same",
				Integer.toString(DATA_GRAM_PORT), deviceAddress.getPort());
		Assert.assertEquals("Expected Device Id and Actual Device Id are not Same", "6945",
				dataModel.getDevice().getDeviceId());
		Assert.assertEquals("Expected Manufacturer and Actual Manufacturer are not Same", TRINETRA,
				dataModel.getDevice().getManufacturer());
		Assert.assertEquals("Expected Model ID and Actual Model ID are not Same", VEHICAL_TRACKING,
				dataModel.getDevice().getModelId());
	}

}
