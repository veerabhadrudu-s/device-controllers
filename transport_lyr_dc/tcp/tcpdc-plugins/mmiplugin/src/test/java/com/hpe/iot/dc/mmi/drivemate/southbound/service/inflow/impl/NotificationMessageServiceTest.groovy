/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.southbound.service.inflow.impl;

import static org.junit.Assert.*
import static org.mockito.Mockito.doAnswer

import java.nio.channels.SocketChannel

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.Answer

import com.hpe.iot.dc.mmi.drivemate.EventIdToNameMapper
import com.hpe.iot.dc.mmi.drivemate.MMIDrivemateServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.drivemate.NotificationMessageService
import com.hpe.iot.dc.mmi.drivemate.UplinkNotificationMessageConverter
import com.hpe.iot.dc.mmi.drivemate.testdata.MMIDrivemateTestDataCollection
import com.hpe.iot.dc.mmi.safemate.northbound.mock.MockIOTRequestResponseHandler
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus
import com.hpe.iot.dc.model.DeviceImpl
import com.hpe.iot.dc.model.DeviceInfo
import com.hpe.iot.dc.model.DeviceModelImpl
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool
import com.hpe.iot.dc.util.DataParserUtility
import com.hpe.iot.m2m.common.RequestPrimitive

/**
 * @author sveera
 *
 */
public class NotificationMessageServiceTest {

	public static final String MSG_TYPE = "notification";
	private static final DeviceImpl DEVICE_UNDER_TEST = new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
	MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2,"123456789012345");

	private NotificationMessageService notificationMessageService;
	private ServerClientSocketPool tcpServerClientSocketPool;

	@Mock
	private IOTPublisherHandler iotPublisherHandler;
	@Mock
	private SocketChannel socketChannel;
	private Answer mockRequestResponseHandler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockRequestResponseHandler=new MockIOTRequestResponseHandler();
		doAnswer(new MockIOTRequestResponseHandler()).when(iotPublisherHandler).
				sendDataToIot(Mockito.any(RequestPrimitive.class));
		//Mockito.doAnswer(new MockSocketWriting()).when(socketChannel).write(Mockito.any(ByteBuffer.class));
		Mockito.when(socketChannel.isConnected()).thenReturn(true);
		Mockito.doAnswer(mockRequestResponseHandler).when(iotPublisherHandler)
				.sendDataToIot(Mockito.any(RequestPrimitive.class));
		ServerSocketToDeviceModel serverSocketToDeviceModel=new MMIDrivemateServerSocketToDeviceModel();
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, new DefaultIOTModelConverterImpl(new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),serverSocketToDeviceModel.getModelId(),serverSocketToDeviceModel.getVersion())));
		tcpServerClientSocketPool=new DefaultTCPServerClientSocketPool();
		TCPServerSocketWriter tcpServerSocketSender = new TCPServerSocketWriter(tcpServerClientSocketPool);
		notificationMessageService = new NotificationMessageService(iotPublisherService,tcpServerSocketSender);
	}

	@Test
	public void testGetMessageType() {
		Assert.assertEquals("Expected Message Type and Actual Message Type are not Same ", MSG_TYPE,
				notificationMessageService.getMessageType());
	}

	@Test
	public void testGetContainerName() {
		assertEquals("Expected Response Message Type and Actual Response Message Type are not Same ", MSG_TYPE,
				notificationMessageService.getContainerName());
	}

	@Test
	public void testExecuteService() {
		UplinkNotificationMessageConverter notificationMessageConverter = new UplinkNotificationMessageConverter(new EventIdToNameMapper());
		DeviceInfo deviceInfo = notificationMessageConverter
				.createModel(DEVICE_UNDER_TEST,DataParserUtility.createBinaryPayloadFromHexaPayload(MMIDrivemateTestDataCollection.NOTIFICATION_PAYLOAD, getClass()));
		tcpServerClientSocketPool.addSocketChannel(deviceInfo.getDevice(), socketChannel);
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = notificationMessageService.executeService(deviceInfo);
		assertNotNull("Failed to execute NotificationMessageService", deviceDataDeliveryStatus);
	}
}
