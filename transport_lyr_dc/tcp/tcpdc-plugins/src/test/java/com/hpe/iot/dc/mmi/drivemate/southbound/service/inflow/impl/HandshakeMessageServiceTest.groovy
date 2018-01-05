/**
 * 
 */
package com.hpe.iot.dc.mmi.drivemate.southbound.service.inflow.impl;

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hpe.iot.dc.mmi.drivemate.HandshakeMessageService;
import com.hpe.iot.dc.mmi.drivemate.MMIDrivemateDataModelTransformer;
import com.hpe.iot.dc.mmi.drivemate.testdata.MMIDrivemateTestDataCollection;
import com.hpe.iot.dc.mmi.utility.TestUtility;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl;
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class HandshakeMessageServiceTest {

	public static final String HAND_SHAKE = "HandShake";
	private HandshakeMessageService handshakeMessageService;
	private ServerClientSocketPool tcpServerClientSocketPool;
	@Mock
	private SocketChannel socketChannel;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(socketChannel.isConnected()).thenReturn(true);
		tcpServerClientSocketPool = new DefaultTCPServerClientSocketPool();
		TCPServerSocketWriter tcpServerSocketSender = new TCPServerSocketWriter(tcpServerClientSocketPool);
		handshakeMessageService = new HandshakeMessageService(tcpServerSocketSender);
	}

	@Test
	public void testExecuteService() throws IOException {

		MMIDrivemateDataModelTransformer mmiDrivemateDataModelTransformer = new MMIDrivemateDataModelTransformer(
				new UplinkDeviceDataConverterFactoryImpl(Collections.<UplinkDeviceDataConverter> emptyList()));
		Device device = new DeviceImpl(MMIDrivemateTestDataCollection.MANUFACTURER,
				MMIDrivemateTestDataCollection.MODEL_ID,MMIDrivemateTestDataCollection.VERSION_2, "123456789012345");
		tcpServerClientSocketPool.addSocketChannel(device, socketChannel);
		List<DeviceInfo> deviceInfo = mmiDrivemateDataModelTransformer.convertToModel(device,
				TestUtility.createDataWithPaddingBytes(getClass(), MMIDrivemateTestDataCollection.HAND_SHAKE_PAYLOAD));
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = handshakeMessageService.executeService(deviceInfo.get(0));
		assertNotNull(deviceDataDeliveryStatus,"Failed to execute " + this.getClass().getName());
	}

	@Test
	public void testGetMessageType() {
		assertEquals(HAND_SHAKE,
				handshakeMessageService.getMessageType(),"Expected Message Type and Actual Message Type are not same");
	}
}
