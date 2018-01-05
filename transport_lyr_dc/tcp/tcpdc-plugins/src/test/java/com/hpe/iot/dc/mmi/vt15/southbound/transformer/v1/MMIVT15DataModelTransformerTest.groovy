/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.southbound.transformer.v1


import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.CONNECTION_PKT_DATA
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.SERIAL_DATA_PKT_DATA
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_BULK_DATA
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA1
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA2
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA3
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA4
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA5
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_HISTORY_PKT_DATA6
import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_LIVE_PKT_DATA
import static org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.vt15.v1.ConnectPacketUplinkDataConverter
import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15DataModelTransformer
import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15ServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.vt15.v1.PacketDataExtractor
import com.hpe.iot.dc.mmi.vt15.v1.SerialPacketDataUplinkDataConverter
import com.hpe.iot.dc.mmi.vt15.v1.TrackingPacketUplinkDataConverter
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModel
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl

/**
 * @author sveera
 *
 */
class MMIVT15DataModelTransformerTest {

	private final DeviceModel deviceModel=new MMIVT15ServerSocketToDeviceModel();
	private final PacketDataExtractor packetDataExtractor=new PacketDataExtractor();
	private final ConnectPacketUplinkDataConverter connectPacketUplinkDataConverter=new ConnectPacketUplinkDataConverter();
	private final TrackingPacketUplinkDataConverter trackingPacketUplinkDataConverter=new TrackingPacketUplinkDataConverter(packetDataExtractor);
	private final SerialPacketDataUplinkDataConverter serialPacketDataUplinkDataConverter=new SerialPacketDataUplinkDataConverter(packetDataExtractor);
	private UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory;
	private MMIVT15DataModelTransformer mmivt15DataModelTransformer;

	@BeforeEach
	public void setUp() throws Exception {
		List<UplinkDeviceDataConverter> uplinkConverters=new ArrayList<>();
		uplinkConverters.add(connectPacketUplinkDataConverter);
		uplinkConverters.add(trackingPacketUplinkDataConverter);
		uplinkConverters.add(serialPacketDataUplinkDataConverter);
		uplinkDeviceDataConverterFactory=new UplinkDeviceDataConverterFactoryImpl(uplinkConverters);
		mmivt15DataModelTransformer=new MMIVT15DataModelTransformer(uplinkDeviceDataConverterFactory);
	}

	@Test
	public void testConvertToModelForBulkTrackingHistoryData() {
		List<DeviceInfo> actualDeviceData=mmivt15DataModelTransformer.convertToModel(deviceModel, TRACKING_HISTORY_PKT_BULK_DATA.getBytes());
		List<DeviceInfo> expectedDeviceData=createExpectedDeviceDataForBulkTrackingHistoryData();
		assertEquals(expectedDeviceData,actualDeviceData,"Expected and Actual device data's are not same");
	}

	@Test
	public void testConvertToModelForTrackingLiveData() {
		List<DeviceInfo> actualDeviceData=mmivt15DataModelTransformer.convertToModel(deviceModel, TRACKING_LIVE_PKT_DATA.getBytes());
		List<DeviceInfo> expectedDeviceData=createExpectedDeviceDataForTrackingLiveData();
		assertEquals(expectedDeviceData,actualDeviceData,"Expected and Actual device data's are not same");
	}

	@Test
	public void testConvertToModelForSerialData() {
		List<DeviceInfo> actualDeviceData=mmivt15DataModelTransformer.convertToModel(deviceModel, SERIAL_DATA_PKT_DATA.getBytes());
		List<DeviceInfo> expectedDeviceData=createExpectedDeviceDataForSerialData();
		assertEquals(expectedDeviceData,actualDeviceData,"Expected and Actual device data's are not same");
	}

	@Test
	public void testConvertToModelForConnectionPktData() {
		List<DeviceInfo> actualDeviceData=mmivt15DataModelTransformer.convertToModel(deviceModel, CONNECTION_PKT_DATA.getBytes());
		List<DeviceInfo> expectedDeviceData=createExpectedDeviceDataForConnectionPktData();
		assertEquals(expectedDeviceData,actualDeviceData,"Expected and Actual device data's are not same");
	}


	private List<DeviceInfo> createExpectedDeviceDataForBulkTrackingHistoryData() {
		List<DeviceInfo> expectedDeviceData=new ArrayList<>();
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA1.getBytes()));
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA2.getBytes()));
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA3.getBytes()));
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA4.getBytes()));
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA5.getBytes()));
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_HISTORY_PKT_DATA6.getBytes()));
		return expectedDeviceData;
	}

	private List<DeviceInfo> createExpectedDeviceDataForTrackingLiveData() {
		List<DeviceInfo> expectedDeviceData=new ArrayList<>();
		expectedDeviceData.add(trackingPacketUplinkDataConverter.createModel(deviceModel, TRACKING_LIVE_PKT_DATA.getBytes()));
		return expectedDeviceData;
	}

	private List<DeviceInfo> createExpectedDeviceDataForSerialData() {
		List<DeviceInfo> expectedDeviceData=new ArrayList<>();
		expectedDeviceData.add(serialPacketDataUplinkDataConverter.createModel(deviceModel, SERIAL_DATA_PKT_DATA.getBytes()));
		return expectedDeviceData;
	}

	private List<DeviceInfo> createExpectedDeviceDataForConnectionPktData() {
		List<DeviceInfo> expectedDeviceData=new ArrayList<>();
		expectedDeviceData.add(connectPacketUplinkDataConverter.createModel(deviceModel, CONNECTION_PKT_DATA.getBytes()));
		return expectedDeviceData;
	}
}
