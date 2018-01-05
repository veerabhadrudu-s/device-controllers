/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.southbound.converter.impl.v1

import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.SERIAL_DATA_PKT_DATA
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15ServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.vt15.v1.PacketDataExtractor
import com.hpe.iot.dc.mmi.vt15.v1.SerialPacketData
import com.hpe.iot.dc.mmi.vt15.v1.SerialPacketDataUplinkDataConverter
import com.hpe.iot.dc.model.Device
import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel

/**
 * @author sveera
 *
 */
class SerialPacketDataUplinkDataConverterTest {

	private static final String SERIAL_PACKET="serial_packet";
	private static final String DEVICE_ID = "170215128"
	private final DeviceModel deviceModel=new MMIVT15ServerSocketToDeviceModel();
	private final Device device=new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion(), DEVICE_ID);
	private final PacketDataExtractor packetDataExtractor=new PacketDataExtractor();
	private SerialPacketDataUplinkDataConverter serialPacketDataUplinkDataConverter;

	@BeforeEach
	public void setUp() {
		serialPacketDataUplinkDataConverter=new SerialPacketDataUplinkDataConverter(packetDataExtractor);
	}

	@Test
	public void testGetMessageType() {
		assertEquals(SERIAL_PACKET,serialPacketDataUplinkDataConverter.getMessageType(),
				"Expected and Actual Message Types are not same");
	}

	@Test
	public void testCreateModelForLiveData() {
		DeviceInfo actualDeviceInfo=serialPacketDataUplinkDataConverter.createModel(deviceModel,SERIAL_DATA_PKT_DATA.getBytes());
		DeviceInfo expectedDeviceInfo=constructExpectedDeviceInfoForLiveData();
		assertNotNull(actualDeviceInfo,"Device Info can't be null");
		assertEquals(expectedDeviceInfo,actualDeviceInfo,"Expected and Actual Device Info are not Equal");
	}

	private DeviceInfo constructExpectedDeviceInfoForLiveData() {
		DeviceInfo deviceInfo=new DeviceInfo(device,SERIAL_PACKET, SERIAL_DATA_PKT_DATA.getBytes());
		DeviceData trackingPacketData=new SerialPacketData("live", "CLIENT_1NS", DEVICE_ID, "20", "28.23434",
				"77.232340", "2017-11-18 09:40:16", "A", "4", "04617037");
		deviceInfo.getDeviceData().put(trackingPacketData.getDeviceDataInformation(), trackingPacketData);;
		return deviceInfo;
	}
}
