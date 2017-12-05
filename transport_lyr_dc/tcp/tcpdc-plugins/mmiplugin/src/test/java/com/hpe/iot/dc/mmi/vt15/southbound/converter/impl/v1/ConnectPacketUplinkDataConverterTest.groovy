/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.southbound.converter.impl.v1

import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.CONNECTION_PKT_DATA
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.hpe.iot.dc.mmi.vt15.v1.ConnectPacketData
import com.hpe.iot.dc.mmi.vt15.v1.ConnectPacketUplinkDataConverter
import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15ServerSocketToDeviceModel
import com.hpe.iot.dc.model.Device
import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel


/**
 * @author sveera
 *
 */
public class ConnectPacketUplinkDataConverterTest {

	private static final String CONNECTION_PACKET = "connection_packet"
	private static final String DEVICE_ID = "101010107"
	private final DeviceModel deviceModel=new MMIVT15ServerSocketToDeviceModel();
	private final Device device=new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion(), DEVICE_ID);
	private ConnectPacketUplinkDataConverter connectPacketUplinkDataConverter;

	@Before
	public void setUp() throws Exception {
		connectPacketUplinkDataConverter=new ConnectPacketUplinkDataConverter();
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected and Actual Message Types are not same",CONNECTION_PACKET,connectPacketUplinkDataConverter.getMessageType());
	}

	@Test
	public void testCreateModel() {
		DeviceInfo actualDeviceInfo=connectPacketUplinkDataConverter.createModel(deviceModel,CONNECTION_PKT_DATA.getBytes());
		DeviceInfo expectedDeviceInfo=constructExpectedDeviceInfo();
		assertNotNull("Device Info can't be null",actualDeviceInfo);
		Assert.assertEquals("Expected and Actual Device Info are not Equal",expectedDeviceInfo,actualDeviceInfo);
	}

	private DeviceInfo constructExpectedDeviceInfo() {
		DeviceInfo deviceInfo=new DeviceInfo(device,CONNECTION_PACKET, CONNECTION_PKT_DATA.getBytes());
		ConnectPacketData connectPacketData=new ConnectPacketData("CLIENT_1ZF", "101010107", "1_35TS2B0164M", "45.118.182.112", 17499,
				"internet", "10 S", "1 M", "9164061023", "9164061023", "0 S", "75 KM", "0 S", "NO", "ON");
		Map<String,DeviceData> deviceData=deviceInfo.getDeviceData();
		deviceData.put(connectPacketData.getDeviceDataInformation(), connectPacketData);
		return deviceInfo;
	}
}
