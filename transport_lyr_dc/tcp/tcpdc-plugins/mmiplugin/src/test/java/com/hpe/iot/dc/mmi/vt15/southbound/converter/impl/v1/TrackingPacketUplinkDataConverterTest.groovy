/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.southbound.converter.impl.v1

import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_LIVE_PKT_DATA
import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15ServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.vt15.v1.PacketDataExtractor
import com.hpe.iot.dc.mmi.vt15.v1.TrackingPacketData
import com.hpe.iot.dc.mmi.vt15.v1.TrackingPacketUplinkDataConverter
import com.hpe.iot.dc.model.Device
import com.hpe.iot.dc.model.DeviceData
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel

/**
 * @author sveera
 *
 */
class TrackingPacketUplinkDataConverterTest {

	private static final String TRACKING_PACKET="tracking_packet";
	private static final String DEVICE_ID = "130329214"
	private final DeviceModel deviceModel=new MMIVT15ServerSocketToDeviceModel();
	private final Device device=new DeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(), deviceModel.getVersion(), DEVICE_ID);
	private final PacketDataExtractor packetDataExtractor=new PacketDataExtractor();
	private TrackingPacketUplinkDataConverter trackingPacketUplinkDataConverter;

	@Before
	public void setUp() {
		trackingPacketUplinkDataConverter=new TrackingPacketUplinkDataConverter(packetDataExtractor);
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected and Actual Message Types are not same",TRACKING_PACKET,trackingPacketUplinkDataConverter.getMessageType());
	}

	@Test
	public void testCreateModelForLiveData() {
		DeviceInfo actualDeviceInfo=trackingPacketUplinkDataConverter.createModel(deviceModel,TRACKING_LIVE_PKT_DATA.getBytes());
		DeviceInfo expectedDeviceInfo=constructExpectedDeviceInfoForLiveData();
		assertNotNull("Device Info can't be null",actualDeviceInfo);
		assertEquals("Expected and Actual Device Info are not Equal",expectedDeviceInfo,actualDeviceInfo);
	}

	@Test
	public void testCreateModelForHistoricalData() {
		DeviceInfo actualDeviceInfo=trackingPacketUplinkDataConverter.createModel(deviceModel,TRACKING_HISTORY_PKT_DATA.getBytes());
		DeviceInfo expectedDeviceInfo=constructExpectedDeviceInfoForHistoricalData();
		assertNotNull("Device Info can't be null",actualDeviceInfo);
		assertEquals("Expected and Actual Device Info are not Equal",expectedDeviceInfo,actualDeviceInfo);
	}

	private DeviceInfo constructExpectedDeviceInfoForLiveData() {
		return constructExpectedDeviceInfo("live", TRACKING_LIVE_PKT_DATA.getBytes());
	}

	private DeviceInfo constructExpectedDeviceInfoForHistoricalData() {
		return constructExpectedDeviceInfo("historical", TRACKING_HISTORY_PKT_DATA.getBytes());
	}

	private DeviceInfo constructExpectedDeviceInfo(String packetType,byte[] rawBinaryData) {
		DeviceInfo deviceInfo=new DeviceInfo(device,TRACKING_PACKET, rawBinaryData);
		DeviceData trackingPacketData=new TrackingPacketData(packetType, "CLIENT_1ZF", DEVICE_ID, "1", "12.962985", "77.576484",
				"2014-01-27 16:54:33", "A", "22", "10", 2.2f, "140", "6", "0.8", 6.0f,
				"0", "0", "0", "0", "1", "0", "0",
				"1", "0", "1", "1", "0", "0",
				12.53f, 4.2f);
		deviceInfo.getDeviceData().put(trackingPacketData.getDeviceDataInformation(), trackingPacketData);;
		return deviceInfo;
	}
}