package com.hpe.iot.dc.mmi.safemate.southbound.service.inflow.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.HEART_BEAT_DATA_MESSAGE_HEX
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.hpe.iot.dc.mmi.safemate.HeartBeatMessageService
import com.hpe.iot.dc.mmi.safemate.HeartBeatPackageConverter
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class HeartBeatMessageServcieTest {

	private static final String MESSAGE_TYPE = "0x4003";
	private static final String MESSAGE_ACK_TYPE = "0x8003";
	private HeartBeatMessageService heartBeatMessageServcie;
	private MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private ServerClientSocketPool tcpServerClientSocketPool;
	@Mock
	private SocketChannel socketChannel;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		/*doAnswer(new MockIOTRequestResponseHandler()).when(requestResponseHandler)
				.sendRequest(Mockito.any(RequestPrimitive.class));
		doAnswer(new MockSocketWriting()).when(socketChannel).write(Mockito.any(ByteBuffer.class));*/
		when(socketChannel.isConnected()).thenReturn(true);
		tcpServerClientSocketPool = new DefaultTCPServerClientSocketPool();
		TCPServerSocketWriter tcpServerSocketSender = new TCPServerSocketWriter(tcpServerClientSocketPool);
		heartBeatMessageServcie = new HeartBeatMessageService(tcpServerSocketSender, mmicrcAlgorithm);
	}

	@Test
	public void testExecuteService() throws IOException {
		HeartBeatPackageConverter heartBeatPackageConverter = new HeartBeatPackageConverter(mmicrcAlgorithm);
		DeviceInfo deviceInfo = heartBeatPackageConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(HEART_BEAT_DATA_MESSAGE_HEX, this.getClass()));
		tcpServerClientSocketPool.addSocketChannel(deviceInfo.getDevice(), socketChannel);
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = heartBeatMessageServcie.executeService(deviceInfo);
		assertNotNull("Failed to execute " + this.getClass().getName(), deviceDataDeliveryStatus);
	}

	@Test
	public void testGetMessageType() {
		assertEquals("Expected Message Type and Actual Message Type are not Same ", MESSAGE_TYPE,
				heartBeatMessageServcie.getMessageType());
	}

}
