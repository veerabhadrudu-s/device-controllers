package com.hpe.iot.dc.mmi.safemate.southbound.service.inflow.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.IPCONNECT_DATA_MESSAGE_HEX
import static com.handson.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.mockito.MockitoAnnotations.initMocks

import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock

import com.hpe.iot.dc.mmi.safemate.IPConnectMessageConverter
import com.hpe.iot.dc.mmi.safemate.IPConnectMessageService
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModelImpl
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool

/**
 * @author sveera
 *
 */
public class IPConnectMessageServiceTest {

	private static final String MESSAGE_TYPE = "0x4001";
	private static final String MESSAGE_ACK_TYPE = "0x8001";
	private static final String CONTAINER_NAME = "ipconnect";

	private IPConnectMessageService ipConnectMessageService;
	private MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private TrackerInfoCreator trackerInfoCreator = new TrackerInfoCreator();
	private ServerClientSocketPool tcpServerClientSocketPool;
	@Mock
	private IOTPublisherHandler iotPublisherHandler;
	@Mock
	private SocketChannel socketChannel;

	@BeforeEach
	public void setUp() throws Exception {
		initMocks(this);
		/*doAnswer(new MockIOTRequestResponseHandler()).when(requestResponseHandler)
		 .sendRequest(Mockito.any(RequestPrimitive.class));*/
		//doAnswer(new MockSocketWriting()).when(socketChannel).write(Mockito.any(ByteBuffer.class));
		//when(socketChannel.isConnected()).thenReturn(true);
		tcpServerClientSocketPool = new DefaultTCPServerClientSocketPool();
		ServerSocketToDeviceModel serverSocketToDeviceModel=new MMIServerSocketToDeviceModel();
		TCPServerSocketWriter tcpServerSocketSender = new TCPServerSocketWriter(tcpServerClientSocketPool);
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, new DefaultIOTModelConverterImpl(new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),serverSocketToDeviceModel.getModelId(),serverSocketToDeviceModel.getVersion())));
		ipConnectMessageService = new IPConnectMessageService(tcpServerSocketSender, mmicrcAlgorithm,
				iotPublisherService);
	}

	@Test
	public void testExecuteService() throws IOException {
		IPConnectMessageConverter ipAddressMetaModelConverter = new IPConnectMessageConverter(mmicrcAlgorithm,
				trackerInfoCreator);
		DeviceInfo deviceInfo = ipAddressMetaModelConverter.createModel(new MMIServerSocketToDeviceModel(),
				createBinaryPayloadFromHexaPayload(IPCONNECT_DATA_MESSAGE_HEX, IPConnectMessageServiceTest.class));
		tcpServerClientSocketPool.addSocketChannel(deviceInfo.getDevice(), socketChannel);
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = ipConnectMessageService.executeService(deviceInfo);
		assertNotNull(deviceDataDeliveryStatus,"Failed to execute IPConnectMessageService");
	}

	@Test
	public void testGetMessageType() {
		assertEquals(MESSAGE_TYPE,ipConnectMessageService.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}
}