/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.southbound.service.inflow.impl

import static com.hpe.iot.dc.mmi.vt15.testdata.MMIVT15TestDataCollection.TRACKING_LIVE_PKT_DATA
import static org.junit.Assert.*
import static org.mockito.Mockito.doAnswer
import static org.mockito.MockitoAnnotations.initMocks

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

import com.hpe.iot.dc.mmi.safemate.northbound.mock.MockIOTRequestResponseHandler
import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15ServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.vt15.v1.MMIVT15UplinkMessageService
import com.hpe.iot.dc.mmi.vt15.v1.PacketDataExtractor
import com.hpe.iot.dc.mmi.vt15.v1.TrackingPacketUplinkDataConverter
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModelImpl
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel
import com.hpe.iot.m2m.common.RequestPrimitive

/**
 * @author sveera
 *
 */
class MMIVT15UplinkMessageServiceTest {

	@Mock
	private IOTPublisherHandler iotPublisherHandler;
	private final TrackingPacketUplinkDataConverter trackingPacketUplinkDataConverter=new TrackingPacketUplinkDataConverter(new PacketDataExtractor());
	private MMIVT15UplinkMessageService mmivt15UplinkMessageService;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		doAnswer(new MockIOTRequestResponseHandler()).when(iotPublisherHandler)
				.sendDataToIot(Mockito.any(RequestPrimitive.class));
		ServerSocketToDeviceModel serverSocketToDeviceModel=new MMIVT15ServerSocketToDeviceModel();
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, new DefaultIOTModelConverterImpl(
				new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),serverSocketToDeviceModel.getModelId(),serverSocketToDeviceModel.getVersion())));
		mmivt15UplinkMessageService=new MMIVT15UplinkMessageService(iotPublisherService);
	}

	@Test
	public void testExecuteService() {
		DeviceInfo deviceInfo = trackingPacketUplinkDataConverter
				.createModel(new MMIVT15ServerSocketToDeviceModel(),TRACKING_LIVE_PKT_DATA.getBytes());
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = mmivt15UplinkMessageService.executeService(deviceInfo);
		assertNotNull("Failed to execute "+MMIVT15UplinkMessageService.class.getSimpleName(), deviceDataDeliveryStatus);
	}
}
